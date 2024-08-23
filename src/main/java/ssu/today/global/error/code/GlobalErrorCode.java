package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    BAD_REQUEST(400, "EG000", "올바르지 않은 요청입니다."),
    INVALID_INPUT(400, "EG001", "올바르지 않은 입력입니다."),
    NOT_FOUND(404, "EG002", "특정 자원이 존재하지 않습니다."),
    METHOD_NOT_ALLOWED(405, "EG003", "허용되지 않은 HTTP method입니다."),
    NETWORK_NOT_CONNECTED(400, "EG004", "네트워크에 연결되어 있지 않습니다."),
    HTTP_HEADER_INVALID(400, "EG005", "request header가 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "EG006", "Request message body가 없거나, 값 타입이 올바르지 않습니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "EG007", "지원하지 않는 Media Type입니다."),
    NOT_ACCEPTABLE(406, "EG008", "헤더에 기입된 형식으로 변환이 불가능합니다."),
    PAYLOAD_TOO_LARGE(413, "EG009", "payload가 너무 큽니다."),
    SERVICE_UNAVAILABLE(503, "EG050", "현재 해당 요청을 서버가 처리할 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "EG051", "내부 서버 오류입니다."),
    UNDEFINED_ERROR(400, "EG100", "정의되지 않은 에러입니다."),
    CLIENT_REGISTRATION_NOT_FOUND(400, "EM000", "해당 registrationId를 가진 ClientRegistration이 존재하지 않습니다."),
    UNAUTHORIZED(401, "EG000", "인증되지 않은 사용자의 요청입니다. 로그인해 주세요.");

    ;
    private final int status;
    private final String code;
    private final String message;

    public static GlobalErrorCode convertToGlobalErrorCode(HttpStatus httpStatus) {
        String name = httpStatus.name();
        for (GlobalErrorCode errorCode : GlobalErrorCode.values()) {
            String errorCodeName = errorCode.name();
            if (name.equals(errorCodeName)) {
                return errorCode;
            }
        }

        return UNDEFINED_ERROR;
    }
}
