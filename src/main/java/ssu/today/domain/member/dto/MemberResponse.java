package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class MemberResponse {

    @Getter
    @AllArgsConstructor
    public static class CheckMemberRegistration {
        private Boolean isRegistered;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NickNameInfo {
        private Long memberId;
        private String nickName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String email;
        private String name;
        private String nickName;
        private String image;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberId {
        private Long memberId;
    }
}
