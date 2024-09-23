package ssu.today.global.result.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssu.today.global.result.ResultCode;

@Getter
@RequiredArgsConstructor
public enum ProfileResultCode implements ResultCode {
    PROFILE_INFO(200, "SP001", "프로필 정보를 성공적으로 조회했습니다."),
    ;
    private final int status;
    private final String code;
    private final String message;
}
