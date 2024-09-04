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
    private String name;

    // Map<String, Object> 형태의 attributes에서 사용자 ID와 이메일을 추출하여 필드를 초기화하는 생성자
    public KakaoInfoResponse(Map<String, Object> attributes) {

        // attributes 맵에서 "id" 키의 값이 존재하면 이를 Long 타입으로 변환하여 authId 필드에 저장하고,
        // 존재하지 않으면 null로 설정
        this.authId = attributes.get("id") != null
                ? Long.valueOf(attributes.get("id").toString())
                : null;

        // attributes 맵에서 "email" 키의 값이 존재하면 이를 String으로 변환하여 email 필드에 저장하고,
        // 존재하지 않으면 빈 문자열을 저장
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.email = kakaoAccount != null && kakaoAccount.get("email") != null
                ? kakaoAccount.get("email").toString()
                : "";

        // 프로필에서 이름 가져오기
        // "properties" 맵에서 프로필 이미지와 이름을 가져옴
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        // 프로필에서 이름 가져오기
        this.name = properties != null && properties.get("nickname") != null
                ? properties.get("nickname").toString()
                : "";

        // attributes 맵에서 "profile_image" 키의 값이 존재하면 이를 String으로 변환하여 profileImageUrl 필드에 저장하고,
        // 존재하지 않으면 빈 문자열을 저장
        this.profileImageUrl = attributes.get("properties") != null
                && ((Map<String, Object>) attributes.get("properties")).get("profile_image") != null
                ? ((Map<String, Object>) attributes.get("properties")).get("profile_image").toString() : "";
    }
}
