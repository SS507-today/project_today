package ssu.today.domain.shareGroup.Scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.repository.ShareGroupRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ssu.today.domain.shareGroup.entity.Status.ACTIVE;
import static ssu.today.domain.shareGroup.entity.Status.PENDING;

@Component
@RequiredArgsConstructor
public class ShareGroupScheduler {

    private final ShareGroupRepository shareGroupRepository;

    /**
     * 매시간 실행되는 스케줄링 작업: 24시간이 지난 PENDING 상태의 그룹을 ACTIVE로 변경하고 초대 코드를 무효화
     * */
    @Scheduled(cron = "30 0 0 * * *") // 매일 자정(00시) 0분 30초마다 스케줄러 실행
    @Transactional
    public void updatePendingGroups() {

        // 현재 시간 계산
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간보다 openAt이 지난 PENDING 상태의 그룹들만 조회
        List<ShareGroup> pendingGroups = shareGroupRepository.findAllByStatusAndOpenAtBefore(PENDING, now);

        // 조회된 각 그룹에 대해 상태를 ACTIVE로 변경하고 초대 코드를 무효화시킴
        for (ShareGroup shareGroup : pendingGroups) {
            updateGroupStatus(shareGroup);
        }
    }

    @Transactional
    public void updateGroupStatus(ShareGroup shareGroup) {
        shareGroup.setStatus(ACTIVE);  // 상태를 ACTIVE로 변경
        shareGroup.setInviteCode("NULL");       // 초대 코드를 무효화
        shareGroupRepository.save(shareGroup); // 변경된 정보를 데이터베이스에 저장
        shareGroupRepository.flush(); // 강제로 변경사항 커밋 ..
    }
}
