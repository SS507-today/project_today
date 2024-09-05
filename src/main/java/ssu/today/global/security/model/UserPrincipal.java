package ssu.today.global.security.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.entity.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
// SecurityContext authentication에 저장될 유저정보
public class UserPrincipal implements UserDetails {
    // Spring Security에서 인증된 사용자 정보를 표현하는 데 사용되는 커스텀 UserDetails 구현 클래스
    // 이 클래스는 사용자의 주요 정보를 보유하고 있으며, 이 정보는 Spring Security의 SecurityContext에서 관리됨
    // 이 객체는 인증된 사용자 세션에 저장되어 애플리케이션에서 사용자 정보를 참조하거나 인증 관련 처리를 할 때 사용
    // Spring Security는 UserDetails 인터페이스를 통해 사용자의 정보를 관리하며,
    // UserPrincipal 클래스는 이 인터페이스를 구현함으로써 Spring Security가 사용자의 인증 및 권한을 관리할 수 있도록 함

    private final Member member; //멤버객체를 가져와 저장
    private Collection<? extends GrantedAuthority> authorities; // 사용자의 권한 목록
    private String password; //현재는 비어 있음

    @Setter
    private Map<String, Object> attributes; // OAuth2와 같은 외부 인증에서 사용되는 사용자 속성을 저장하는 맵.
    //OAuth2와 같은 외부 인증에서 사용되는 사용자 속성을 저장

    // 멤버객체와 권한 목록을 가져와서 저장
    public UserPrincipal(Member member, Collection<? extends GrantedAuthority> authorities) {
        this.member = member;
        this.authorities = authorities;
    }

    // MemberResponse 객체로부터 UserPrincipal 객체를 생성하는 팩토리 메소드
    // UserPrincipal 객체를 직접 생성하지 않고, create 메소드를 통해 생성함으로써, 객체 생성 로직을 캡슐화
    public static UserPrincipal create(Member member) {

        // 사용자의 권한 목록을 생성. 여기서는 기본적으로 "ROLE_USER" 권한을 부여
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.getRole()));

        // UserPrincipal 객체를 생성하고 반환
        return new UserPrincipal(member, authorities);
    }

    // 계정이 만료되지 않았음을 나타냄. true를 반환하여 계정이 유효함을 나타냄
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않음을 나타냄
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명이 만료되지 않았음을 나타냄
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되었음을 나타냄
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Spring Security가 사용자 이름으로 사용하는 값을 반환. 여기서는 이메일을 사용
    @Override
    public String getUsername() {
        return member.getEmail();
    }
}
