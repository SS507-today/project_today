package ssu.today.domain.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import ssu.today.domain.member.dto.MemberResponse;

@Mapper
public interface MemberMapper {
    void save(MemberResponse.LoginInfo loginInfo);
    MemberResponse.LoginInfo findById(Long id);
    MemberResponse.LoginInfo findByRefreshToken(String refreshToken);
    void update(MemberResponse.LoginInfo loginInfo);
    void updateRefreshToken(MemberResponse.LoginInfo loginInfo);
}
