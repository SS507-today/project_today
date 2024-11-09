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
    DIARY_LIST(200, "SP000", "성공적으로 특정 번들의 다이어리 리스트를 조회하였습니다."),
    DIARY_BUNDLE_LIST(200, "SP0000", "성공적으로 다이어리 번들 리스트를 조회하였습니다."),
    DIARY_BUNDLE(200, "SP0000", "성공적으로 최신 번들을 조회하였습니다."),
    TAGGED_MEMBERS_LIST(200, "SP006", "성공적으로 태그된 멤버 리스트를 조회했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
