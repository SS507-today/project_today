package ssu.today.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.result.ResultResponse;

import static ssu.today.global.result.code.MemberResultCode.CHECK_MEMBER_REGISTRATION;
import static ssu.today.global.result.code.MemberResultCode.LOGIN;
import static ssu.today.global.result.code.MemberResultCode.SIGNUP;

@RestController
@RequestMapping("/auth")
@Tag(name = "00. 인증,인가 관련 API", description = "회원의 회원가입 및 로그인 등을 처리하는 API입니다.")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/signup/android")
    @Operation(summary = "회원가입 API(안드로이드)", description = "안드로이드 클라이언트가 사용하는 회원가입 API입니다.")
    public ResultResponse<MemberResponse.LoginInfo> signup(@Valid @RequestBody MemberRequest.SignupRequest request) {
        return ResultResponse.of(SIGNUP, memberService.signup(request));
    }

    @PostMapping("/login/android")
    @Operation(summary = "로그인 API", description = "안드로이드 클라이언트가 로그인하는 API입니다.")
    public ResultResponse<MemberResponse.LoginInfo> login(@Valid @RequestBody MemberRequest.LoginRequest request) {
        return ResultResponse.of(LOGIN, memberService.login(request));
    }

    @PostMapping("/check-registration")
    @Operation(summary = "회원가입 여부 조회 API", description = "authId를 통해, 해당 정보와 일치하는 회원의 가입 여부를 조회하는 API입니다.")
    public ResultResponse<MemberResponse.CheckMemberRegistration> checkSignup(@Valid @RequestBody MemberRequest.LoginRequest request) {
        return ResultResponse.of(CHECK_MEMBER_REGISTRATION, memberService.checkRegistration(request));
    }
}

