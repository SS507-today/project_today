package ssu.today.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ssu.today.domain.member.dto.OauthRequest;
import ssu.today.domain.member.dto.OauthResponse;
import ssu.today.domain.member.dto.RefreshTokenResponse;
import ssu.today.domain.member.dto.UserDTO;
import ssu.today.domain.member.entity.Member;

public interface MemberService {
    OauthResponse login(String provider, OauthRequest oauthRequest, HttpServletResponse response);
    RefreshTokenResponse tokenRefresh(HttpServletRequest request);
    UserDTO getMyInfo();
    Member findMemberByAuthId(Long authId);
}
