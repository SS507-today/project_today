package ssu.today.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ssu.today.global.error.BusinessException;
import ssu.today.global.security.model.UserPrincipal;

import static ssu.today.global.error.code.GlobalErrorCode.BAD_REQUEST;
import static ssu.today.global.error.code.GlobalErrorCode.UNAUTHORIZED;

public class SecurityUtil {
    // 현재 인증된 사용자의 ID를 가져오는 유틸리티 클래스
    // 스프링 시큐리티와 관련된 작업을 간단하게 처리할 수 있도록 도와줌
    // 주된 목적은 스프링 시큐리티 컨텍스트에서 현재 로그인된 사용자의 정보를 안전하게 추출하는 것
    // 특히, 스프링 시큐리티를 사용하는 애플리케이션에서는 SecurityContextHolder를 통해 현재 사용자의 인증 정보를 얻을 수 있음
    // 이 클래스는 이러한 과정을 추상화하여 쉽게 현재 사용자의 ID를 얻을 수 있도록 함

    // private 생성자를 통해 클래스의 인스턴스화 방지 (유틸리티 클래스이므로 인스턴스를 만들 필요가 없음)
    private SecurityUtil() {}

    // 현재 인증된 사용자의 ID를 반환하는 메소드
    public static long getCurrentUserId() {

        // SecurityContextHolder를 통해 현재 스프링 시큐리티 컨텍스트에서 인증 정보를 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) { // 인증 정보가 없으면 인증되지 않은 사용자로 간주하고 예외 발생
            throw new BusinessException(UNAUTHORIZED);
        }

        long userId;
        // 인증된 사용자의 정보를 UserPrincipal 객체로 변환하여 사용자 ID를 추출
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getAuthId();
        } else {
            // 인증된 정보가 예상한 타입(UserPrincipal의 인스턴스)이 아니면 잘못된 요청으로 간주하고 예외 발생
            throw new BusinessException(BAD_REQUEST);
        }

        // 추출된 사용자 ID 반환
        return userId;
    }
}
