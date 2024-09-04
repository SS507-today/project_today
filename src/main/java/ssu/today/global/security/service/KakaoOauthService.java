package ssu.today.global.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import ssu.today.domain.member.converter.MemberConverter;
import ssu.today.domain.member.dto.KakaoInfoResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class KakaoOauthService {
    // 카카오 API를 호출하여 사용자의 프로필 정보를 가져오고, 이를 바탕으로 사용자 데이터를 관리하는 클래스
    // 카카오 OAuth를 사용하여 사용자의 정보를 가져오고, 해당 정보를 바탕으로 데이터베이스에 사용자 정보를 저장하거나 업데이트하는 기능을 제공

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;

    @Transactional(readOnly = true)
    // 카카오 API를 호출하여 Access Token을 사용해 유저 정보를 가져오는 메소드
    public Map<String, Object> getUserAttributesByToken(String accessToken){
        // WebClient를 사용하여 HTTP 요청을 보냄
        // WebClient는 Spring에서 비동기 방식으로 HTTP 요청을 처리하는 클라이언트
        return WebClient.create()
                .get() // GET 요청을 설정
                .uri("https://kapi.kakao.com/v2/user/me") // 카카오 API의 사용자 정보 요청 URI
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken)) // HTTP 헤더에 Bearer 인증 타입으로 Access Token을 추가
                .retrieve() // 서버로부터의 응답을 가져옴
                // 응답 본문을 Mono로 받아옴. 이때 응답을 Map<String, Object> 형태로 변환
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block(); // 비동기 작업을 동기 방식으로 처리하여 결과를 반환
    }

    // 카카오 API로부터 가져온 유저 정보를 데이터베이스에 저장하거나 업데이트하는 메소드
    public UserDTO getUserProfileByToken(String accessToken){

        // Access Token을 사용하여 카카오 API로부터 사용자 정보를 가져옴
        Map<String, Object> userAttributesByToken = getUserAttributesByToken(accessToken);

        // 가져온 사용자 정보를 기반으로 KakaoInfoDto 객체를 생성
        KakaoInfoResponse kakaoInfoDto = new KakaoInfoResponse(userAttributesByToken);

        // KakaoInfoDto 객체에서 필요한 정보를 추출하여 UserDto 객체를 생성
        UserDTO userDTO = UserDTO.builder()
                .authId(kakaoInfoDto.getAuthId()) // 카카오 사용자 authID를 UserDto의 authID로 설정
                .name(kakaoInfoDto.getName())
                . email(kakaoInfoDto.getEmail()) // 카카오 사용자 이메일을 UserDto의 이메일로 설정
                .image(kakaoInfoDto.getProfileImageUrl())
                .platform("kakao")
                .build();

        // 데이터베이스에서 해당 사용자 ID로 사용자를 조회
        Member existingUser = memberRepository.findByAuthId(userDTO.getAuthId()).orElse(null);

        if (existingUser != null) {
            // 이미 존재하면 업데이트
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setImage(userDTO.getImage());
            existingUser.setPlatform(userDTO.getPlatform());
            existingUser.setName(userDTO.getName());
            memberRepository.save(existingUser);
        } else {
            // 존재하지 않으면 새로운 사용자로 저장
            Member newUser = memberConverter.toEntity(userDTO);
            memberRepository.save(newUser);
        }

        return userDTO;
    }
}
