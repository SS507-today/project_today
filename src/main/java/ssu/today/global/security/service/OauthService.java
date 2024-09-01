package ssu.today.global.security.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.global.error.BusinessException;

import static ssu.today.global.error.code.JwtErrorCode.INVALID_REFRESH_TOKEN;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class OauthService {
    private final JwtTokenService jwtTokenService;
    private final KakaoOauthService kakaoOauthService;
    private final MemberRepository memberRepository;

    //카카오 로그인
    public String loginWithKakao(String accessToken, HttpServletResponse response) {
        //액세스 토큰으로 사용자 정보 가져오기
        UserDTO loginInfo = kakaoOauthService.getUserProfileByToken(accessToken);
        //가져온 사용자 정보를 바탕으로 Access Token과 Refresh Token을 생성하여 반환
        return getTokens(loginInfo.getAuthId(), response);
    }

    //액세스토큰, 리프레시토큰 생성하고 DB에 저장
    public String getTokens(Long id, HttpServletResponse response) {
        //사용자의 ID를 바탕으로 Access Token을 생성
        final String accessToken = jwtTokenService.createAccessToken(id.toString());
        //Refresh Token을 생성
        final String refreshToken = jwtTokenService.createRefreshToken();

        // 사용자의 정보를 JPA를 통해 조회
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        // 현재 사용자 정보에 새로운 refresh token 추가
        member.setRefreshToken(refreshToken);

        // 사용자 정보를 DB에 저장 (업데이트)
        memberRepository.save(member);

        // 생성된 Refresh Token을 클라이언트의 쿠키에 저장
        jwtTokenService.addRefreshTokenToCookie(refreshToken, response);
        // 생성된 Access Token을 반환하여 클라이언트에게 전달
        return accessToken;
    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token을 기반으로 사용자 조회
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        // refresh token이 유효하지 않으면 예외
        if(!jwtTokenService.validateToken(refreshToken)) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 유효한 Refresh Token을 기반으로 새로운 Access Token을 생성하여 반환
        return jwtTokenService.createAccessToken(member.getAuthId().toString());
    }
}