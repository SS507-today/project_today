package ssu.today.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public abstract class MemberResponse {
    @Getter
    @AllArgsConstructor
    public static class CheckMemberRegistration {
        private Boolean isRegistered;
    }

    @Getter
    @AllArgsConstructor
    public static class MemberId {
        private Long memberId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginInfo {
        private Long memberId;
        private String accessToken;
        private String refreshToken;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class DeleteMemberInfo {
        private Long memberId;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime deletedAt;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MemberInfo { //특정 회원 조회
        private Long memberId;
        private String name;
        private String email;
        private String image;
    }
}
