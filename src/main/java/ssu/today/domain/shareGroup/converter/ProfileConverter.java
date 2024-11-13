package ssu.today.domain.shareGroup.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.today.domain.shareGroup.dto.ProfileResponse;
import ssu.today.domain.shareGroup.entity.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // 태그된 프로필 리스트 정보 반환 DTO
    public ProfileResponse.TaggedProfileList toTaggedProfileList(Long diaryId, List<Profile> profiles) {
        List<ProfileResponse.TaggedProfile> taggedProfileList = profiles.stream()
                .map(profile -> ProfileResponse.TaggedProfile
                        .builder()
                        .profileId(profile.getId())
                        .name(profile.getProfileNickName())
                        .image(profile.getImage())
                        .build())
                .collect(Collectors.toList());

        return ProfileResponse.TaggedProfileList
                .builder()
                .diaryId(diaryId)
                .taggedMembersList(taggedProfileList)
                .build();
    }

    // 프로필 업데이트

    public ProfileResponse.UpdateProfile toUpdateProfile(Profile profile) {
        return ProfileResponse.UpdateProfile
                .builder()
                .shareGroupId(profile.getShareGroup().getId())
                .profileId(profile.getId())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
