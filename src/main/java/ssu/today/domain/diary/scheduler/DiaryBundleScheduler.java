package ssu.today.domain.diary.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.diary.repository.DiaryBundleRepository;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.repository.ProfileRepository;
import ssu.today.domain.shareGroup.service.ShareGroupService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiaryBundleScheduler {

    private final DiaryBundleRepository diaryBundleRepository;
    private final ShareGroupService shareGroupService;
    private final ProfileRepository profileRepository;

    // 모든 공유 그룹에 대해 번들을 생성하거나 업데이트하는 함수
    // 자정 5분 후 매일 실행
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void createBundlesForAllShareGroups() {
        // active 상태인 모든 공유 그룹을 가져옴
        List<ShareGroup> activeShareGroups = shareGroupService.getActiveShareGroups();

        // 각 공유 그룹에 대해 번들 생성 작업 수행
        for (ShareGroup shareGroup : activeShareGroups) {
            createOrUpdateBundleForShareGroup(shareGroup);
            updateIsMyTurnForShareGroup(shareGroup);  // isMyTurn 필드 업데이트
        }
    }

    // 각 공유 그룹에서 최신 번들을 확인하고 새로운 번들이 필요한 경우 생성하는 함수
    private void createOrUpdateBundleForShareGroup(ShareGroup shareGroup) {

        // 1. 제일 최신 번들 가져오기
        DiaryBundle latestBundle = diaryBundleRepository.findFirstByShareGroupOrderByStartedAtDesc(shareGroup)
                .orElse(null);

        // 2. 번들을 새로 생성해야 하는지 확인
        if (latestBundle == null || shouldCreateNewBundle(latestBundle)) {
            // 첫 번들이거나 번들을 만들 조건이 되면, 새로운 번들 생성
            createNewBundle(shareGroup);
        }
    }

    // 새로운 번들을 생성하는 함수
    private void createNewBundle(ShareGroup shareGroup) {

        // 현재 시간을 기준으로 번들의 시작과 종료일 설정
        LocalDateTime startedAt = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘 자정
        LocalDateTime endedAt = startedAt.plusDays(shareGroup.getProfileList().size());

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
     * @return 새로운 번들을 생성해야 하면 true를 반환
     */
    private boolean shouldCreateNewBundle(DiaryBundle latestBundle) {
        // 최신 번들의 종료일이 지났으면 새로운 번들을 생성해야 함
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(latestBundle.getEndedAt());
    }

    // 공유 그룹 멤버들의 isMyTurn 필드를 업데이트하는 함수
    private void updateIsMyTurnForShareGroup(ShareGroup shareGroup) {
        // 공유 그룹의 모든 멤버 가져오기 (profileId 기준으로 정렬해서 순서 보장 문제 해결)
        List<Profile> profileList = shareGroupService.findProfileListByShareGroupId(shareGroup.getId())
                .stream() //리스트는 sorted연산 사용 불가, stream 사용
                .sorted(Comparator.comparing(Profile::getId))  // profileId 순으로 정렬
                .toList();

        // 공유 그룹의 시작일을 기준으로 몇 번째 날인지 계산 (년도가 아니라 오픈일 기준이라, 년도변경시 초기화되는 문제 해결)
        LocalDate startDate = shareGroup.getOpenAt().toLocalDate();
        int daysSinceStart = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now());
        int dayOfBundle = daysSinceStart % profileList.size();  // 나머지 연산으로 몇 번째 날인지 계산

        // 각 멤버의 차례를 설정 (하루에 한 명씩 돌아가면서 isMyTurn 설정)
        for (int i = 0; i < profileList.size(); i++) {
            Profile profile = profileList.get(i);

            // 자신의 차례이면 isMyTurn을 true로 설정, 아니면 false로 설정
            if (i == dayOfBundle) {
                profile.setIsMyTurn(true);
            } else {
                profile.setIsMyTurn(false);
            }

            profileRepository.save(profile);  // 변경사항 저장
        }
    }


}