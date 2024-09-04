package ssu.today.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
