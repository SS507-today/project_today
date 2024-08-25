package ssu.today.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.converter.MemberConverter;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.global.error.BusinessException;
import ssu.today.global.security.util.JwtUtils;

import java.util.List;

import static ssu.today.global.error.code.MemberErrorCode.MEMBER_ALREADY_SIGNUP;
import static ssu.today.global.error.code.MemberErrorCode.MEMBER_NOT_FOUND_BY_AUTH_ID_AND_SOCIAL_TYPE;
import static ssu.today.global.error.code.MemberErrorCode.MEMBER_NOT_FOUND_BY_MEMBER_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JwtUtils jwtUtils;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long ACCESS_TOKEN_VALIDITY_IN_SECONDS;

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;

    @Override
    @Transactional
    public MemberResponse.LoginInfo signup(MemberRequest.SignupRequest request) {
        if (memberRepository.existsByAuthId(request.getAuthId())) {
            throw new BusinessException(MEMBER_ALREADY_SIGNUP);
        }

        Member member = memberConverter.toEntity(request);
        memberRepository.save(member);

        Long memberId = member.getId();
        // 회원가입 완료 후 로그인 처리를 위해 access token, refresh token 발급
        // 별도 권한 정책이 없으므로 default 처리
        String role = "ROLE_DEFAULT";

        return createJwtAndGetLoginInfo(memberId, role);
    }


    @Override
    public MemberResponse.LoginInfo login(MemberRequest.LoginRequest request) {
        Member member = findMember(request.getAuthId());
        Long memberId = member.getId();
        String role = "ROLE_DEFAULT";
        return createJwtAndGetLoginInfo(memberId, role);
    }

    private MemberResponse.LoginInfo createJwtAndGetLoginInfo(Long memberId, String role) {
        String accessToken = jwtUtils.createJwt(memberId, role, ACCESS_TOKEN_VALIDITY_IN_SECONDS);

        return memberConverter.toLoginInfo(memberId, accessToken);
    }

    @Override
    public MemberResponse.CheckMemberRegistration checkRegistration(MemberRequest.LoginRequest request) {
        boolean isRegistered = memberRepository.existsByAuthId(request.getAuthId());
        return memberConverter.toCheckMemberRegistration(isRegistered);
    }


    @Override
    public MemberResponse.MemberInfo getMyInfo(Member member) {
        return memberConverter.toMemberInfo(member);
    }

    @Override
    public MemberResponse.MemberId getMyMemberId(Member member) {
        return memberConverter.toMemberId(member.getId());
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND_BY_MEMBER_ID));
    }

    @Override
    public Member findMember(String authId) {
        return memberRepository.findByAuthId(authId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND_BY_AUTH_ID_AND_SOCIAL_TYPE));
    }

}

