package ssu.today.global.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.global.security.model.MemberDetails;

/**
 * JWT를 헤더로 세팅하여 요청이 들어왔을 때,
 * JwtAuthenticationFilter -> JwtUtils의 getAuthentication(String token)
 * -> loadUserByUsername(String username) 호출을 통해 DB에 저장된 회원인지 확인하는 클래스
 */
@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /**
     *
     * @param username 회원을 식별하기 위한 데이터. PK 값인 memberId
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(username)) // 전달된 memberId를 Long 타입으로 변환
                .orElseThrow(() -> new UsernameNotFoundException("해당 memberId를 가진 회원이 존재하지 않습니다."));

        return new MemberDetails(member);
    }
}
