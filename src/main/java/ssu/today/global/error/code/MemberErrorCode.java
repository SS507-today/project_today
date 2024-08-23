package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND_BY_MEMBER_ID(404, "EM001", "해당 memberId를 가진 회원이 존재하지 않습니다."),
    MEMBER_NOT_FOUND_BY_EMAIL(404, "EM002", "해당 이메일을 가진 회원이 존재하지 않습니다."),
    MEMBER_NOT_FOUND_BY_AUTH_ID_AND_SOCIAL_TYPE(404, "EM003", "해당 authId와 socialType을 가진 회원이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "EM000", "해당 refresh token이 존재하지 않습니다."),
    INVALID_SOCIAL_TYPE(404, "EM000", "해당 socialType은 유효하지 않습니다."),
    MEMBER_ALREADY_SIGNUP(400, "EM000", "해당 이메일을 가진 회원은 이미 회원가입하였습니다."),
    TEMP_MEMBER_INFO_COOKIE_NOT_FOUND(404, "EM000", "회원가입에 사용할 temp-member-info 쿠키가 존재하지 않습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}
