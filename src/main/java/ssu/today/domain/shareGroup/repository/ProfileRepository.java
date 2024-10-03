package ssu.today.domain.shareGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.Role;
import ssu.today.domain.shareGroup.entity.ShareGroup;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 그룹의 생성자(Profile) 조회 (role이 CREATOR인 프로필을 가져옴)
    Optional<Profile> findByShareGroupAndRole(ShareGroup shareGroup, Role role);
    boolean existsByShareGroupIdAndMemberId(Long shareGroupId, Long memberId);
    List<Profile> findByMemberId(Long memberId);
    Optional<Profile> findByShareGroupIdAndMemberId(Long shareGroupId, Long memberId);
    List<Profile> findByShareGroupId(Long shareGroupId);
    // 특정 프로필 ID 리스트로 프로필 목록 조회
    List<Profile> findByIdIn(List<Long> profileIds);
}