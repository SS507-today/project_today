package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {
    DIARY_BUNDLE_NOT_FOUND(400, "EJ001", "다이어리 bundle을 찾을 수 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}
