package ssu.today.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.OauthRequest;
import ssu.today.domain.member.dto.OauthResponse;
import ssu.today.domain.member.dto.RefreshTokenResponse;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.security.service.OauthService;
import ssu.today.global.error.BusinessException;
import ssu.today.global.security.util.SecurityUtil;

import java.util.Arrays;

import static ssu.today.global.error.code.JwtErrorCode.INVALID_REFRESH_TOKEN;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;

@RestController
@RequestMapping
@Tag(name = "00. 인증,인가 관련 API", description = "회원의 회원가입 및 로그인 등을 처리하는 API입니다.")
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;
    private final MemberService memberService;

    @PostMapping("/login/oauth/{provider}")
    @Operation(summary = "로그인 API", description = "카카오톡을 통해 서비스에 로그인하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "privider", description = "kakao를 입력해 주세요. (추후 확장 가능성 고려)")
    })
    public OauthResponse login(@PathVariable String provider, @RequestBody OauthRequest oauthRequest,
                               HttpServletResponse response) { //HTTP 응답 조작: Refresh Token을 클라이언트의 브라우저 쿠키에 저장할 때 사용
        OauthResponse oauthResponse = new OauthResponse(); //응답객체 생성
        switch (provider) {
            case "kakao":
                //프론트에서 받은 액세스 토큰으로 서버 자체 액세스 토큰 생성
                String accessToken = oauthService.loginWithKakao(oauthRequest.getAccessToken(), response);
                oauthResponse.setAccessToken(accessToken);
        }
        return oauthResponse;
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/token/refresh")
    @Operation(summary = "액세스토큰 재발급 API", description = "리프레시 토큰으로 액세스토큰을 재발급 받는 API입니다.")
    public RefreshTokenResponse tokenRefresh(HttpServletRequest request) { //request: 클라이언트가 서버에 보낸 HTTP 요청에 포함된 쿠키를 가져오기 위함

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

    // 현재 유저정보 조회 API
    @GetMapping("/my/info")
    public MemberResponse.LoginInfo info() {
        // SecurityContext에서 현재 로그인된 사용자의 ID를 추출
        final long memberId = SecurityUtil.getCurrentUserId();

        //userId를 사용하여 데이터베이스에서 해당 사용자의 정보를 조회
        MemberResponse.LoginInfo loginInfo = memberService.findById(memberId);
        if(loginInfo == null) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }
        return loginInfo;
    }
}