package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
}
