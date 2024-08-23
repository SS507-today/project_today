package ssu.today.domain.member.service;

import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.entity.Member;

public interface MemberService {
    MemberResponse.LoginInfo signup(MemberRequest.SignupRequest request);
    MemberResponse.LoginInfo login(MemberRequest.LoginRequest request);
    MemberResponse.CheckMemberRegistration checkRegistration(MemberRequest.LoginRequest request);
    MemberResponse.MemberInfo getMyInfo(Member member);
    MemberResponse.MemberId getMyMemberId(Member member);
    Member findMember(Long memberId);
    Member findMember(String authId);
    // Member deleteMember(Member member);
}
