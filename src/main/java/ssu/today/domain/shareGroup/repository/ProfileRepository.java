package ssu.today.domain.shareGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.Role;
import ssu.today.domain.shareGroup.entity.ShareGroup;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 그룹의 생성자(Profile) 조회 (role이 CREATOR인 프로필을 가져옴)
    Optional<Profile> findByShareGroupAndRole(ShareGroup shareGroup, Role role);
}