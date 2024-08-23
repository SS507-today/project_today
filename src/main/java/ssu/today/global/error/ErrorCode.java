package ssu.today.global.error;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
