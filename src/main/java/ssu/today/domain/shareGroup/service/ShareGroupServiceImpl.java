package ssu.today.domain.shareGroup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.converter.ShareGroupConverter;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;
import ssu.today.domain.shareGroup.repository.ShareGroupRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShareGroupServiceImpl implements ShareGroupService {

    private final ShareGroupConverter shareGroupConverter;
    private final ShareGroupRepository shareGroupRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); //스케줄러

    @Transactional
    @Override
    public ShareGroup createShareGroup(ShareGroupRequest.createShareGroupRequest request,
                                       Member member) {

        // 초대링크를 위한 고유번호 생성 (UUID)
        String inviteCode = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // 엔티티로 변환하는 로직
        ShareGroup newShareGroup = shareGroupConverter.toEntity(request, inviteCode);

        // 생성된 공유 그룹을 DB에 저장
        newShareGroup = shareGroupRepository.save(newShareGroup);

        // 24시간 후에 상태를 ACTIVE로 변경하고 초대 코드를 무효화하는 스케줄링 작업
        scheduleStatusUpdate(newShareGroup);

        return newShareGroup;
    }

    //24시간 후에 그룹 상태를 ACTIVE로 변경하고 초대 코드를 무효화하는 작업을 스케줄링하는 메서드
    private void scheduleStatusUpdate(ShareGroup shareGroup) {
        // 현재 시간으로부터 24시간 후에 실행될 작업을 스케줄링
        long delay = Duration.between(LocalDateTime.now(), shareGroup.getCreatedAt().plusHours(24)).toMillis();

        scheduler.schedule(() -> {
            shareGroup.setStatus(Status.ACTIVE);  // 상태를 ACTIVE로 변경
            shareGroup.setInviteCode(null);       // 초대 코드를 무효화
            shareGroupRepository.save(shareGroup); // 변경된 정보를 데이터베이스에 저장
        }, delay, TimeUnit.MILLISECONDS);  // 정확히 24시간 후 실행되도록 설정
    }
}
