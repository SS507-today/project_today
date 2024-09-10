package ssu.today.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShareGroupErrorCode implements ErrorCode {
    MEMBER_COUNT_ERROR(400, "EG001", "멤버 수는 2~6명 사이여야 합니다."),
    MEMBER_COUNT_MISMATCH(400, "EG002", "멤버 수와 멤버 이름 리스트의 크기가 일치하지 않습니다."),
    SHARE_GROUP_NOT_FOUND(404, "EG003", "공유 그룹을 찾을 수 없습니다."),
    PROFILE_NOT_FOUND(404, "EG004", "프로필을 찾을 수 없습니다."),
    INVALID_PROFILE_FOR_GROUP(400, "EG005", "해당 프로필은 이 공유 그룹에 속하지 않습니다."),
    SHARE_GROUP_ALREADY_STARTED(400, "EG006", "공유 그룹이 생성된지 24시간이 지나 참여할 수 없습니다."),
    SHARE_GROUP_CREATOR_NOT_FOUND(400, "EG007", "공유 그룹 생성자의 프로필이 존재하지 않습니다."),
    UNAUTHORIZED_DELETE(403, "EG008", "공유 그룹을 삭제할 권한이 없습니다."),
    ALREADY_JOINED(400, "EG009", "이미 해당 공유 그룹에 참여하였습니다.");
    ;

    private final int status;
    private final String code;
    private final String message;
}
