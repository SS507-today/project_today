package ssu.today.domain.diary.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.diary.repository.DiaryBundleRepository;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.repository.ShareGroupRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiaryBundleScheduler {

    private final ShareGroupRepository shareGroupRepository;
    private final DiaryBundleRepository diaryBundleRepository;

    // 모든 공유 그룹에 대해 번들을 생성하거나 업데이트하는 함수
    // 자정 5분 후 매일 실행
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void createBundlesForAllShareGroups() {
        // 모든 공유 그룹을 가져옴
        List<ShareGroup> allShareGroups = shareGroupRepository.findAll();

        // 각 공유 그룹에 대해 번들 생성 작업 수행
        for (ShareGroup shareGroup : allShareGroups) {
            createOrUpdateBundleForShareGroup(shareGroup);
        }
    }

    // 각 공유 그룹에서 최신 번들을 확인하고 새로운 번들이 필요한 경우 생성하는 함수
    private void createOrUpdateBundleForShareGroup(ShareGroup shareGroup) {

        LocalDateTime now = LocalDateTime.now();

        // 1. 공유 그룹의 openAt이 현재보다 이전이면 번들을 생성할 시점인지 확인
        if (now.isAfter(shareGroup.getOpenAt())) {

            // 2. 제일 최신 번들 가져오기
            DiaryBundle latestBundle = diaryBundleRepository.findFirstByShareGroupOrderByStartedAtDesc(shareGroup)
                    .orElse(null);

            // 3. 번들을 새로 생성해야 하는지 확인
            if (latestBundle == null || shouldCreateNewBundle(latestBundle, shareGroup)) {
                // 새로운 번들 생성
                createNewBundle(shareGroup);
            }
        }
    }

    // 새로운 번들을 생성하는 함수
    private void createNewBundle(ShareGroup shareGroup) {

        // 인원 수에 따른 시간 계산 (24 * 인원수) 시간마다 생성해야 함
        int hoursInterval = 24 * shareGroup.getMemberCount();

        // 현재 시간을 기준으로 번들의 시작과 종료일 설정
        LocalDateTime startedAt = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘 자정
        LocalDateTime endedAt = startedAt.plusDays(shareGroup.getMemberCount() - 1);

        DiaryBundle newBundle = DiaryBundle.builder()
                .shareGroup(shareGroup)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();

        diaryBundleRepository.save(newBundle);
    }

    /**
     * 새로운 번들을 생성해야 하는지 여부를 결정하는 함수
     * @param latestBundle 가장 최신 번들
     * @param shareGroup 공유 그룹
     * @return 새로운 번들을 생성해야 하면 true를 반환
     */
    private boolean shouldCreateNewBundle(DiaryBundle latestBundle, ShareGroup shareGroup) {
        // 최신 번들의 종료일이 지났으면 새로운 번들을 생성해야 함
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(latestBundle.getEndedAt());
    }
}

