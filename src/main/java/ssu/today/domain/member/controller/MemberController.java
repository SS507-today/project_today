package ssu.today.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.converter.MemberConverter;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.MemberResultCode;
import ssu.today.global.security.annotation.LoginMember;

import static ssu.today.global.result.code.MemberResultCode.CHECK_MEMBER_REGISTRATION;

@RestController
@RequestMapping("/members")
@Tag(name = "01. 회원 API", description = "회원 도메인의 API입니다.")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberConverter memberConverter;

    // 닉네임 설정 API
    @PostMapping("/my/nickName")
    @Operation(summary = "닉네임 생성/변경 API", description = "닉네임을 생성 또는 변경하는 API입니다.")
    public ResultResponse<MemberResponse.NickNameInfo> setNickName(@LoginMember Member member,
                                                                   @RequestParam String nickName) {
        return ResultResponse.of(MemberResultCode.SET_NICKNAME, memberService.setNickName(member, nickName));
    }

    // 현재 로그인된 유저정보 조회 API (authId로)
    @GetMapping("/my")
    @Operation(summary = "현재 유저정보 조회 API", description = "현재 저장된 멤버의 authId로 유저정보를 조회하는 API입니다.")
    public ResultResponse<UserDTO> getMyInfo() {
        return ResultResponse.of(CHECK_MEMBER_REGISTRATION, memberService.getMyInfo());
    }

    // memberId를 통해 유저정보 조회 API
    @GetMapping("/{memberId}") // memberId를 사용해 특정 회원 정보 조회
    @Operation(summary = "특정 회원 정보 조회 API", description = "memberId로 유저 정보를 조회하는 api입니다.")
    public ResultResponse<MemberResponse.MemberInfo> getMemberInfo(@PathVariable(name = "memberId") Long memberId) {
        Member member = memberService.findMemberByMemberId(memberId);
        return ResultResponse.of(MemberResultCode.MEMBER_INFO,
                memberConverter.toMemberInfo(member));
    }

    // 회원탈퇴 API
    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴 API", description = "서비스에서 회원 탈퇴하는 API 입니다.")
    public ResultResponse<MemberResponse.MemberId> deleteMember(@LoginMember Member member) {

        Member deleteMember = memberService.deleteMember(member);
        return ResultResponse.of(MemberResultCode.DELETE_MEMBER,
                memberConverter.toMemberId(deleteMember));
    }
}
