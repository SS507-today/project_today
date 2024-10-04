package ssu.today.global.result.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.result.ResultCode;

@Getter
@RequiredArgsConstructor
public enum DiaryResultCode implements ResultCode {
    CREATE_PRESIGNED_URL(200, "SP000", "성공적으로 Presigned URL을 요청하였습니다."),
    UPLOAD_DIARY(200, "SP000", "성공적으로 다이어리를 업로드하였습니다."),
    RETRIEVE_DIARY(200, "SP000", "성공적으로 다이어리를 조회하였습니다."),
    DELETE_DIARY(200, "SP000", "성공적으로 다이어리를 삭제하였습니다."),
    DIARY_LIST(200, "SP000", "성공적으로 다이어리 번들을 조회하였습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}
