package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {
    DIARY_BUNDLE_NOT_FOUND(400, "EJ001", "다이어리 bundle을 찾을 수 없습니다."),
    INVALID_TAG_PROFILE(400, "EJ002", "태그한 프로필이 존재하지 않습니다. 다시 입력해 주세요."),
    DIARY_NOT_FOUND(400,"EJ003", "해당 다이어리가 존재하지 않습니다." ),
    NOT_YOUR_TURN(400, "EJ004", "현재 다이어리를 업로드할 차례가 아닙니다."),
    ALREADY_EXISTS_DIARY(400, "EJ005", "이미 이 번들 안에서 다이어리를 작성했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
