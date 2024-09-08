package ssu.today.domain.shareGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {
    Optional<ShareGroup> findByInviteCode(String inviteCode);
    // 24시간이 지난 PENDING 상태의 모든 그룹을 조회
    List<ShareGroup> findAllByStatusAndCreatedAtBefore(Status status, LocalDateTime dateTime);
}
