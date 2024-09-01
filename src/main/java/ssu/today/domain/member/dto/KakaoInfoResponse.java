package ssu.today.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoInfoResponse {
    private Long authId;
    private String email;
    private String profileImageUrl;

    // Map<String, Object> 형태의 attributes에서 사용자 ID와 이메일을 추출하여 필드를 초기화하는 생성자
    public KakaoInfoResponse(Map<String, Object> attributes) {

        // attributes 맵에서 "id" 키의 값을 Long 타입으로 변환하여 id 필드에 저장
        this.authId = Long.valueOf(attributes.get("id").toString());
        // attributes 맵에서 "email" 키의 값이 존재하면 이를 String으로 변환하여 email 필드에 저장하고,
        // 존재하지 않으면 빈 문자열을 저장
        this.email = attributes.get("email") != null ? attributes.get("email").toString() : "";
        // attributes 맵에서 "profile_image" 키의 값이 존재하면 이를 String으로 변환하여 profileImageUrl 필드에 저장하고,
        // 존재하지 않으면 빈 문자열을 저장
        this.profileImageUrl = attributes.get("properties") != null
                && ((Map<String, Object>) attributes.get("properties")).get("profile_image") != null
                ? ((Map<String, Object>) attributes.get("properties")).get("profile_image").toString() : "";
    }
}
