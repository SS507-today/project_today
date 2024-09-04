package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    AUTHENTICATION_TYPE_IS_NOT_BEARER(400, "EJ001", "인증 타입이 Bearer가 아닙니다."),
    ACCESS_TOKEN_IS_EXPIRED(401, "EJ002", "액세스 토큰이 만료되었습니다."),
    MEMBER_NOT_FOUND(404, "EJ003", "해당 회원이 존재하지 않습니다. 탈퇴한 회원인지 확인해 주세요."),
    INVALID_ACCESS_TOKEN(404, "EJ004", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(404, "EJ005", "유효하지않은 리프레시 토큰입니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
