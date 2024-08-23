package ssu.today.domain.member.converter;

import org.springframework.stereotype.Component;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.entity.Member;

@Component
public class MemberConverter {
    public Member toEntity(MemberRequest.SignupRequest request) {
        return Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .image(request.getImage())
                .authId(request.getAuthId())
                .build();
    }

    public MemberResponse.MemberInfo toMemberInfo(Member member) {
        return MemberResponse.MemberInfo.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .image(member.getImage())
                .build();
    }

    public MemberResponse.LoginInfo toLoginInfo(Long memberId, String accessToken) {
        return MemberResponse.LoginInfo.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .build();
    }

    public MemberResponse.MemberId toMemberId(Long memberId) {
        return new MemberResponse.MemberId(memberId);
    }

    public MemberResponse.CheckMemberRegistration toCheckMemberRegistration(boolean isRegistered) {
        return new MemberResponse.CheckMemberRegistration(isRegistered);
    }

}
