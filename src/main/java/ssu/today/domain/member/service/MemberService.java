package ssu.today.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ssu.today.domain.member.dto.MemberRequest;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.dto.OauthRequest;
import ssu.today.domain.member.dto.OauthResponse;
import ssu.today.domain.member.dto.RefreshTokenResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;

public interface MemberService {
    OauthResponse login(String provider, OauthRequest oauthRequest, HttpServletResponse response);
    RefreshTokenResponse tokenRefresh(HttpServletRequest request);
    UserDTO getMyInfo();
    MemberResponse.CheckMemberRegistration checkRegistration(MemberRequest.LoginRequest request);
    MemberResponse.NickNameInfo setNickName(Member member, String nickName);
    Member findMemberByAuthId(Long authId);
}
