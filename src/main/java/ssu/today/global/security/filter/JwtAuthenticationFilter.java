package ssu.today.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import ssu.today.global.error.ErrorCode;
import ssu.today.global.security.util.JwtUtils;

import java.io.IOException;
import java.util.List;

import static ssu.today.global.error.code.JwtErrorCode.ACCESS_TOKEN_IS_EXPIRED;
import static ssu.today.global.error.code.JwtErrorCode.AUTHENTICATION_TYPE_IS_NOT_BEARER;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEALTH_CHECK_URL = "/";
    private static final List<String> EXCLUDE_URL_PATTERN_LIST = List.of(
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs",
            "/auth");
    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtUtils jwtUtils;
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith(AUTHORIZATION_TYPE)) {
            handleException(request, response, filterChain, AUTHENTICATION_TYPE_IS_NOT_BEARER);
            return;
        }

        String jwt = authorization.substring(AUTHORIZATION_TYPE.length());
        log.info("jwt: {}", jwt);

        if (jwtUtils.isExpired(jwt)) {
            handleException(request, response, filterChain, ACCESS_TOKEN_IS_EXPIRED);
            return;
        }

        final Authentication authentication;
        try {
            authentication = jwtUtils.getAuthentication(jwt);
        } catch (UsernameNotFoundException e) {
            handleException(request, response, filterChain, MEMBER_NOT_FOUND);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain, ErrorCode errorCode) throws ServletException, IOException{
        SecurityContextHolder.clearContext();
        request.setAttribute("authException", errorCode);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Swagger 관련 경로를 필터링에서 제외
        return EXCLUDE_URL_PATTERN_LIST.stream()
                .anyMatch(urlPattern -> path.startsWith(urlPattern)) || path.equals(HEALTH_CHECK_URL);

    }
}

