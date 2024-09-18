package ssu.today.domain.shareGroup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // openedAt 시간이 지난 pending 그룹들 조회
    List<ShareGroup> findAllByStatusAndOpenAtBefore(Status status, LocalDateTime dateTime);
    //페이징 처리해서 shareGroup 가져오기
    Page<ShareGroup> findByIdIn(List<Long> shareGroupIds, Pageable pageable);
    Page<ShareGroup> findByIdInAndStatus(List<Long> ids, Status status, Pageable pageable);
}
