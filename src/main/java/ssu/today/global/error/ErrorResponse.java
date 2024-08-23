package ssu.today.global.error;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    // @JsonInclude(JsonInclude.Include.NON_EMPTY)
    // 매핑할 값이 없으면 안드로이드 쪽에서 별도로 구현해야 하기 때문에 위 어노테이션 주석 처리
    private final List<ValidationError> data;


    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }

        public static ValidationError of(final ObjectError objectError) {
            return ValidationError.builder()
                    .field(objectError.getObjectName())
                    .message(objectError.getDefaultMessage())
                    .build();
        }
    }
}
