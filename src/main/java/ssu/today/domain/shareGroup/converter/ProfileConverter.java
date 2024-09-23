package ssu.today.domain.shareGroup.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.today.domain.shareGroup.dto.ProfileResponse;
import ssu.today.domain.shareGroup.entity.Profile;

@Component
@RequiredArgsConstructor
public class ProfileConverter {

    // 프로필 정보 반환 DTO
    public ProfileResponse.ProfileInfo toProfileInfo(Profile profile) {
        return ProfileResponse.ProfileInfo.builder()
                .shareGroupId(profile.getShareGroup().getId())
                .profileId(profile.getId())
                .profileNickName(profile.getProfileNickName())
                .image(profile.getImage())
                .description(profile.getDescription())
                .role(profile.getRole())
                .isMyTurn(profile.getIsMyTurn())
                .joinedAt(profile.getJoinedAt())
                .build();
    }
}
