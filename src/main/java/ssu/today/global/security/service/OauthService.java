package ssu.today.global.security.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.error.BusinessException;

import static ssu.today.global.error.code.JwtErrorCode.INVALID_REFRESH_TOKEN;

@RequiredArgsConstructor
@Service
public class OauthService {
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;
    private final KakaoOauthService kakaoOauthService;

    //카카오 로그인
    public String loginWithKakao(String accessToken, HttpServletResponse response) {
        //액세스 토큰으로 사용자 정보 가져오기
        MemberResponse.LoginInfo loginInfo = kakaoOauthService.getUserProfileByToken(accessToken);
        //가져온 사용자 정보를 바탕으로 Access Token과 Refresh Token을 생성하여 반환
        return getTokens(loginInfo.getAuthId(), response);
    }

    //액세스토큰, 리프레시토큰 생성
    public String getTokens(Long id, HttpServletResponse response) {
        //사용자의 ID를 바탕으로 Access Token을 생성
        final String accessToken = jwtTokenService.createAccessToken(id.toString());
        //Refresh Token을 생성
        final String refreshToken = jwtTokenService.createRefreshToken();

        //사용자 정보를 조회하여 Refresh Token을 업데이트
        MemberResponse.LoginInfo loginInfo = memberService.findById(id);
        loginInfo.setRefreshToken(refreshToken); //현재 사용자 정보에 새로운 refresh token 추가
        memberService.updateRefreshToken(loginInfo); //위 사용자 정보를 DB에 업데이트

        // 생성된 Refresh Token을 클라이언트의 쿠키에 저장
        jwtTokenService.addRefreshTokenToCookie(refreshToken, response);
        // 생성된 Access Token을 반환하여 클라이언트에게 전달
        return accessToken;
    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token을 기반으로 사용자 조회
        MemberResponse.LoginInfo loginInfo = memberService.findByRefreshToken(refreshToken);

        // 사용자가 존재하지 않으면 예외
        if(loginInfo == null) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // refresh token이 유효하지 않으면 예외
        if(!jwtTokenService.validateToken(refreshToken)) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 유효한 Refresh Token을 기반으로 새로운 Access Token을 생성하여 반환
        return jwtTokenService.createAccessToken(loginInfo.getAuthId().toString());
    }
}