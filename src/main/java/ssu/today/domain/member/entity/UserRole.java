package ssu.today.domain.member.entity;

public enum UserRole {

    //일반 사용자와 관리자 권한 부여
    USER("user"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
