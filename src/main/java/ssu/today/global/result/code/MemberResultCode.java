package ssu.today.global.result.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.result.ResultCode;

@Getter
@RequiredArgsConstructor
public enum MemberResultCode implements ResultCode {
    LOGIN(200, "SM000", "성공적으로 로그인하였습니다."),
    REFRESH_TOKEN(200, "SM001", "성공적으로 리프레쉬 토큰을 발급했습니다."),
    SET_NICKNAME(200, "SM002", "성공적으로 닉네임을 생성했습니다."),
    MYPAGE_INFO(200, "SM003", "내 정보를 성공적으로 조회하였습니다."),
    EDIT_MYPAGE_INFO(200, "SM004", "내 정보를 성공적으로 수정하였습니다."),
    CHECK_MEMBER_REGISTRATION(200, "SM005", "해당 정보에 대응하는 회원의 가입 여부를 성공적으로 조회하였습니다."),
    MEMBER_INFO(200, "SM006", "회원 정보를 성공적으로 조회하였습니다."),
    DELETE_MEMBER(200, "SM007", "성공적으로 탈퇴하였습니다."),
    GET_MY_MEMBERID(200, "SM008", "자신의 memberId를 성공적으로 조회하였습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
