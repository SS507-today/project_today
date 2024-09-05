package ssu.today.domain.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.converter.MemberConverter;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.OauthRequest;
import ssu.today.domain.member.dto.OauthResponse;
import ssu.today.domain.member.dto.RefreshTokenResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.global.error.BusinessException;
import ssu.today.global.security.service.OauthService;
import ssu.today.global.security.util.SecurityUtil;

import java.util.Arrays;

import static ssu.today.global.error.code.JwtErrorCode.INVALID_REFRESH_TOKEN;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;
import static ssu.today.global.error.code.MemberErrorCode.MEMBER_NOT_FOUND_BY_AUTH_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final OauthService oauthService;
    private final MemberConverter memberConverter;

    @Transactional
    @Override
    public OauthResponse login(String provider, OauthRequest oauthRequest, HttpServletResponse response) {
        OauthResponse oauthResponse = new OauthResponse(); //응답객체 생성
        switch (provider) {
            case "kakao":
                //프론트에서 받은 액세스 토큰으로 서버 자체 액세스 토큰 생성
                String accessToken = oauthService.loginWithKakao(oauthRequest.getAccessToken(), response);
                oauthResponse.setAccessToken(accessToken);
        }

        return oauthResponse;
    }

    @Transactional
    @Override
    public RefreshTokenResponse tokenRefresh(HttpServletRequest request) {
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(); //응답 객체 생성

        // 클라이언트가 보낸 쿠키 목록 가져오기
        Cookie[] list = request.getCookies();

        if(list == null) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 쿠키 목록에서 "refresh_token"이라는 이름의 쿠키를 필터링하여 가져옴
        Cookie refreshTokenCookie = Arrays.stream(list)
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .toList().get(0);

        if(refreshTokenCookie == null) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // OauthService를 사용하여 Refresh Token을 기반으로 새로운 Access Token을 생성
        String accessToken = oauthService.refreshAccessToken(refreshTokenCookie.getValue());
        refreshTokenResponse.setAccessToken(accessToken);
        return refreshTokenResponse;
    }

    @Override
    public UserDTO getMyInfo() {
        // SecurityContext에서 현재 로그인된 사용자의 ID를 추출
        final long authId = SecurityUtil.getCurrentUserId();

        //userId를 사용하여 데이터베이스에서 해당 사용자의 정보를 조회
        Member member = findMemberByAuthId(authId);
        if(member == null) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }
        return memberConverter.toMemberInfo(member);
    }

    @Transactional
    @Override
    public MemberResponse.CheckMemberRegistration checkRegistration(MemberRequest.LoginRequest request) {
        boolean isRegistered = memberRepository.existsByAuthId(request.getAuthId());
        if (!isRegistered) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }
        return memberConverter.toCheckMemberRegistration(isRegistered);
    }

    @Transactional
    @Override
    public MemberResponse.NickNameInfo setNickName(Member member, String nickName) {
        // 닉네임을 설정하고 db에 저장
        member.setNickName(nickName);
        memberRepository.save(member);

        return memberConverter.toNickNameInfo(member);
    }

    @Override
    public Member findMemberByAuthId(Long authId) {
        return memberRepository.findByAuthId(authId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND_BY_AUTH_ID));
    }
}

