package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public abstract class MemberResponse {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class LoginInfo {
        private Long authId;
        private String email;
        private String image; //카카오 프로필 이미지
        private String platform;
        private String refreshToken;
    }
}
