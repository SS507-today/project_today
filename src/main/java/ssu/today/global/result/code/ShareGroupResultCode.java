package ssu.today.global.result.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.result.ResultCode;

@Getter
@RequiredArgsConstructor
public enum ShareGroupResultCode implements ResultCode {
    CREATE_SHARE_GROUP(200, "SG001", "성공적으로 공유 그룹을 생성하였습니다."),
    SHARE_GROUP_INFO(200, "SG002", "공유 그룹을 성공적으로 조회했습니다."),
    JOIN_SHARE_GROUP(200, "SG003", "성공적으로 공유 그룹에 참여했습니다." ),
    SHARE_GROUP_INFO_LIST(200, "SG004", "내가 참여한 공유 그룹 목록을 성공적으로 조회하였습니다."),
    DELETE_SHARE_GROUP(200, "SG005", "성공적으로 공유 그룹을 삭제했습니다."),
    GET_INVITE_CODE(200, "SG006", "성공적으로 초대 코드를 조회하였습니다.")
    ;
    private final int status;
    private final String code;
    private final String message;
}
