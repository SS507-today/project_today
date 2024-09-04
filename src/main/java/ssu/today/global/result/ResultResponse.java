package ssu.today.global.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResultResponse<T> {
    private final int status;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ResultResponse<T> of(ResultCode resultCode, T data) {
        return ResultResponse.<T>builder()
                .status(resultCode.getStatus())
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ResultResponse<T> of(ResultCode resultCode) {
        return ResultResponse.<T>builder()
                .status(resultCode.getStatus())
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(null)
                .build();
    }
}
