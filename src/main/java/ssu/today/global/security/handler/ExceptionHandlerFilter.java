package ssu.today.global.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import ssu.today.global.error.BusinessException;
import ssu.today.global.error.ErrorResponse;

import java.io.IOException;
import java.util.Collections;

//Spring Security 필터 체인에서 발생하는 예외를 처리하기 위해 설계된 필터
//이 필터는 요청이 처리되는 도중에 발생하는 예외를 잡아서, 적절한 HTTP 응답 코드와 에러 메시지를 클라이언트에게 반환하는 역할
//OncePerRequestFilter를 상속받아 한 번의 요청당 한 번만 실행되는 필터로 동작

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    // 이 메소드는 요청이 필터를 통과할 때마다 호출됨. 여기서 예외 처리 진행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청을 필터 체인의 다음 필터로 전달
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException ex) {
            // CustomException이 발생하면 해당 예외를 처리하는 메소드를 호출
            setErrorResponse(response, ex);
        } catch (Exception ex) {
            // 그 외의 예외가 발생하면 내부 서버 오류로 처리
            setErrorResponse(response, ex);
        }
    }

    // HTTP 응답에 에러 상태와 메시지를 설정하는 메소드
    public void setErrorResponse(HttpServletResponse response, Throwable ex) throws IOException {
        // 예외 메시지를 로그에 기록
        logger.error("[ExceptionHandlerFilter] errMsg : " + ex.getMessage());

        // ErrorResponse 객체를 생성하여 예외 정보를 설정
        ErrorResponse errorResponse;
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            errorResponse = ErrorResponse
                    .builder()
                    .status(businessException.getErrorCode().getStatus()) // BusinessException의 상태 코드를 설정
                    .code(businessException.getErrorCode().getCode())     // 에러 코드 설정
                    .message(businessException.getErrorCode().getMessage()) // 에러 메시지 설정
                    .data(Collections.emptyList()) // ValidationError 리스트를 비어있는 리스트로 설정
                    .build();
        } else {
            errorResponse = ErrorResponse
                    .builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // 일반 예외에 대해 500 상태 코드 사용
                    .code("INTERNAL_SERVER_ERROR") // 코드로 INTERNAL_SERVER_ERROR 설정
                    .message(ex.getMessage()) // 예외 메시지를 설정
                    .data(Collections.emptyList()) // ValidationError 리스트를 비어있는 리스트로 설정
                    .build();
        }

        // 응답 상태 코드를 설정
        response.setStatus(errorResponse.getStatus());
        // 응답의 콘텐츠 타입을 JSON으로 설정
        response.setContentType("application/json; charset=UTF-8");

        // 응답 본문에 에러 메시지를 JSON 형식으로 작성하여 반환
        response.getWriter().write(
                convertToJson(errorResponse)  // ErrorResponse 객체를 JSON 형식으로 변환하여 반환
        );
    }

    // ErrorResponse 객체를 JSON 형식으로 변환하는 메소드.
    private String convertToJson(ErrorResponse errorResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(errorResponse); // ErrorResponse 객체를 JSON으로 변환
        } catch (JsonProcessingException e) {
            logger.error("Error converting ErrorResponse to JSON", e);
            return "{}";  // 변환 중 오류가 발생하면 빈 JSON 객체를 반환합니다.
        }
    }
}
