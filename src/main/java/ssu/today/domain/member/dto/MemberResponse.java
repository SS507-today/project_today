package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class MemberResponse {

    @Getter
    @AllArgsConstructor
    public static class CheckMemberRegistration {
        private Boolean isRegistered;
    }
}
