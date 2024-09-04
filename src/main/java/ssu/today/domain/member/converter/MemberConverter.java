package ssu.today.domain.member.converter;

import org.springframework.stereotype.Component;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;

@Component
public class MemberConverter {

    // 인가 관련
    public UserDTO toMemberInfo(Member member) {
        return UserDTO.builder()
                .authId(member.getAuthId())
                .image(member.getImage())
                .email(member.getEmail())
                .platform(member.getPlatform())
                .name(member.getName())
                .refreshToken(member.getRefreshToken())
                .build();
    }

    // 인가 관련, DTO를 엔티티로 변환하는 메소드
    public Member toEntity(UserDTO userDTO) {
        return Member.builder()
                .authId(userDTO.getAuthId()) // UserDTO의 authId를 User 엔티티의 authId로 설정
                .name(userDTO.getName()) //카카오톡 이름
                .email(userDTO.getEmail()) // UserDTO의 이메일을 User 엔티티의 이메일로 설정
                .image(userDTO.getImage()) // UserDTO의 이미지를 User 엔티티의 이미지로 설정
                .platform(userDTO.getPlatform()) // UserDTO의 플랫폼을 User 엔티티의 플랫폼으로 설정
                .refreshToken(userDTO.getRefreshToken()) // UserDTO의 리프레시 토큰을 User 엔티티의 리프레시 토큰으로 설정
                .build();
    }

    public MemberResponse.CheckMemberRegistration toCheckMemberRegistration(boolean isRegistered) {
        return new MemberResponse.CheckMemberRegistration(isRegistered);
    }


}
