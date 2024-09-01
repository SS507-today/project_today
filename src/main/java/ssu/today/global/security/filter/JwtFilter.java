package ssu.today.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.domain.member.service.MemberService;
import ssu.today.global.error.BusinessException;
import ssu.today.global.error.ErrorCode;
import ssu.today.global.security.model.UserPrincipal;
import ssu.today.global.security.service.JwtTokenService;

import java.io.IOException;

import static ssu.today.global.error.code.JwtErrorCode.INVALID_ACCESS_TOKEN;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    // JWT(JSON Web Token)를 사용한 인증을 처리하는 필터로,
    // HTTP 요청이 애플리케이션에 도달하기 전에 JWT 토큰의 유효성을 검사하고 해당 토큰이 유효하다면, 사용자 정보를 SecurityContext에 저장하는 역할
    // 이 필터는 Spring Security 필터 체인에 추가되어, 모든 요청에 대해 JWT를 검사하는 데 사용됩
    // JwtFilter 클래스는 Spring Security 필터 체인에 추가되어 모든 HTTP 요청에 대해 JWT 인증을 수행

    public static final String AUTHORIZATION_HEADER = "Authorization"; // Authorization 헤더의 이름을 정의한 상수
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;

    // 이 메소드는 요청이 필터를 통과할 때마다 호출됨
    // 여기서 JWT(액세스 토큰)의 유효성을 검사하고, 사용자 계정정보를 SecurityContext에 저장
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // HttpServletRequest로 변환하여 HTTP 요청 정보를 가져옴
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // 요청 URL을 로깅함 (HTTP 요청이 들어왔을 때, 해당 요청이 어떤 URL로 들어왔는지를 기록(log))
        logger.info("[JwtFilter] : " + httpServletRequest.getRequestURL().toString());
        // 요청 헤더에서 JWT 토큰을 추출
        String jwt = resolveToken(httpServletRequest);

        // JWT가 존재하고, 유효한 경우
        if (StringUtils.hasText(jwt) && jwtTokenService.validateToken(jwt)) {

            // JWT에서 사용자 ID를 추출
            Long authId = Long.valueOf(jwtTokenService.getPayload(jwt)); // 토큰에 있는 authId 가져오기


            // 추출한 사용자 ID로 데이터베이스에서 사용자 정보를 조회
            Member member = memberRepository.findById(authId)
                    .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

            // 조회한 사용자 정보를 UserPrincipal로 변환하여 UserDetails 객체를 생성
            UserDetails userDetails = UserPrincipal.create(member);
            // 사용자 인증 토큰을 생성하고, 이를 SecurityContext에 설정하여 인증 정보를 저장
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            // JWT가 유효하지 않으면 예외 발생
            throw new BusinessException(INVALID_ACCESS_TOKEN);
        }

        // 필터 체인의 다음 필터를 호출하여 요청을 계속 처리
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하는 메소드
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 JWT 토큰을 가져옴
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 토큰이 존재하고 "Bearer "로 시작하는 경우, "Bearer " 부분을 제외한 실제 JWT 토큰을 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 토큰이 없거나 올바르지 않은 경우 null을 반환
        return null;
    }
}