package ssu.today.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.dto.MemberResponse;
import ssu.today.domain.member.mapper.MemberMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper mapper;
    public void save(MemberResponse.LoginInfo loginInfo) {
        mapper.save(loginInfo);
    }
    @Transactional(readOnly = true)
    public MemberResponse.LoginInfo findById(Long id) {
        return mapper.findById(id);
    }
    @Transactional(readOnly = true)
    public MemberResponse.LoginInfo findByRefreshToken(String refreshToken) {
        return mapper.findByRefreshToken(refreshToken);
    }
    public void update(MemberResponse.LoginInfo loginInfo) {
        mapper.update(loginInfo);
    }
    public void updateRefreshToken(MemberResponse.LoginInfo loginInfo) {
        mapper.updateRefreshToken(loginInfo);
    }
}

