package ssu.today.domain.shareGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.shareGroup.entity.ShareGroup;

import java.util.Optional;

@Repository
public interface ShareGroupRepository extends JpaRepository<ShareGroup, Long> {
    Optional<ShareGroup> findByInviteCode(String inviteCode);
}
