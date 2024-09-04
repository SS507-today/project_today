package ssu.today.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.member.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAuthId(Long authId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Boolean existsByAuthId(Long authId);
}
