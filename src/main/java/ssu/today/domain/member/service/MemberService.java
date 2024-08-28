package ssu.today.domain.member.service;

import ssu.today.domain.member.dto.MemberResponse;

public interface MemberService {
    void save(MemberResponse.LoginInfo loginInfo);
    MemberResponse.LoginInfo findById(Long id);
    MemberResponse.LoginInfo findByRefreshToken(String refreshToken);
    void update(MemberResponse.LoginInfo loginInfo);
    void updateRefreshToken(MemberResponse.LoginInfo loginInfo);
}
