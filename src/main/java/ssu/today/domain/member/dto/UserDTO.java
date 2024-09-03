package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDTO {
    private Long authId;
    private String email;
    private String image; //카카오 프로필 이미지
    private String platform;
    private String refreshToken;
    private String name; //카카오 설정 이름
}
