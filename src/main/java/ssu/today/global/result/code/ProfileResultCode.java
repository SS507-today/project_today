package ssu.today.global.result.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.result.ResultCode;

@Getter
@RequiredArgsConstructor
public enum ProfileResultCode implements ResultCode {
    PROFILE_INFO(200, "SP001", "프로필 정보를 성공적으로 조회했습니다."),
    PROFILE_INFO_LIST(200, "SP002", "프로필 리스트를 성공적으로 조회했습니다."),
    UPDATE_PROFILE(200, "SP003", "프로필을 성공적으로 업데이트했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
