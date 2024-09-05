package ssu.today.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.MemberResultCode;
import ssu.today.global.security.annotation.LoginMember;

import static ssu.today.global.result.code.MemberResultCode.CHECK_MEMBER_REGISTRATION;

@RestController
@RequestMapping
@Tag(name = "01. 회원 API", description = "회원 도메인의 API입니다.")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 닉네임 설정 API
    @PostMapping("/members/my/nickName")
    @Operation(summary = "닉네임 생성/변경 API", description = "닉네임을 생성 또는 변경하는 API입니다.")
    public ResultResponse<MemberResponse.NickNameInfo> setNickName(@LoginMember Member member,
                                                                   @RequestParam String nickName) {
        return ResultResponse.of(MemberResultCode.SET_NICKNAME, memberService.setNickName(member, nickName));
    }

    // 현재 로그인된 유저정보 조회 API (authId로)
    @GetMapping("/my/info")
    @Operation(summary = "현재 유저정보 조회 API", description = "현재 저장된 멤버의 authId로 유저정보를 조회하는 API입니다.")
    public ResultResponse<UserDTO> getMyInfo() {
        return ResultResponse.of(CHECK_MEMBER_REGISTRATION, memberService.getMyInfo());
    }
}
