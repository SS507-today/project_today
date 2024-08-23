package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    AUTHENTICATION_TYPE_IS_NOT_BEARER(400, "EJ000", "인증 타입이 Bearer가 아닙니다."),
    ACCESS_TOKEN_IS_EXPIRED(401, "EJ000", "액세스 토큰이 만료되었습니다."),
    MEMBER_NOT_FOUND(404, "EJ000", "해당 memberId를 가진 회원이 존재하지 않습니다. 탈퇴한 회원인지 확인해 주세요."),

    ;

    private final int status;
    private final String code;
    private final String message;
}
