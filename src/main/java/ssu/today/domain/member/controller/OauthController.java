package ssu.today.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.converter.MemberConverter;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.OauthRequest;
import ssu.today.domain.member.dto.OauthResponse;
import ssu.today.domain.member.dto.RefreshTokenResponse;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.MemberResultCode;
import ssu.today.global.security.annotation.LoginMember;

import static ssu.today.global.result.code.MemberResultCode.CHECK_MEMBER_REGISTRATION;
import static ssu.today.global.result.code.MemberResultCode.LOGIN;
import static ssu.today.global.result.code.MemberResultCode.REFRESH_TOKEN;

@RestController
@RequestMapping
@Tag(name = "00. 인증,인가 관련 API", description = "회원의 회원가입 및 로그인 등을 처리하는 API입니다.")
@RequiredArgsConstructor
public class OauthController {

    private final MemberService memberService;
    private final MemberConverter memberConverter;

    @PostMapping("/login/oauth/{provider}")
    @Operation(summary = "로그인 API", description = "카카오톡을 통해 서비스에 로그인하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "provider", description = "kakao를 입력해 주세요.")
    })
    public ResultResponse<OauthResponse> login(@PathVariable(name = "provider") String provider, @RequestBody OauthRequest oauthRequest,
                               HttpServletResponse response) { //HTTP 응답 조작: Refresh Token을 클라이언트의 브라우저 쿠키에 저장할 때 사용
        return ResultResponse.of(LOGIN, memberService.login(provider, oauthRequest, response));
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/token/refresh")
    @Operation(summary = "액세스토큰 재발급 API", description = "리프레시 토큰으로 액세스토큰을 재발급 받는 API입니다.")
    public ResultResponse<RefreshTokenResponse> tokenRefresh(HttpServletRequest request) { //request: 클라이언트가 서버에 보낸 HTTP 요청에 포함된 쿠키를 가져오기 위함
        return ResultResponse.of(REFRESH_TOKEN, memberService.tokenRefresh(request));
    }

    // 특정 authId를 가진 회원의 회원가입 여부 조회
    @PostMapping("/auth/check-registration")
    @Operation(summary = "회원가입 여부 조회 API", description = "authId를 통해, 해당 정보와 일치하는 회원의 가입 여부를 조회하는 API입니다.")
    public ResultResponse<MemberResponse.CheckMemberRegistration> checkSignup(@Valid @RequestBody MemberRequest.LoginRequest request) {
        return ResultResponse.of(CHECK_MEMBER_REGISTRATION, memberService.checkRegistration(request));
    }
}