package ssu.today.domain.shareGroup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.diary.service.DiaryService;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.converter.ProfileConverter;
import ssu.today.domain.shareGroup.dto.ProfileResponse;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.service.ShareGroupService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.ProfileResultCode;
import ssu.today.global.security.annotation.LoginMember;

import java.util.List;

import static ssu.today.global.result.code.DiaryResultCode.TAGGED_MEMBERS_LIST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@Tag(name = "03. 프로필 관련 API", description = "프로필 조회, 수정 등의 작업을 처리하는 API입니다.")
public class ProfileController {

    private final ShareGroupService shareGroupService;
    private final ProfileConverter profileConverter;
    private final DiaryService diaryService;

    @GetMapping("/{profileId}")
    @Operation(summary = "특정 profileId로 프로필 조회 API", description = "profileId로 특정 프로필을 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "profileId", description = "조회할 프로필의 id를 입력해 주세요.")
    })
    public ResultResponse<ProfileResponse.ProfileInfo> getProfileInfo(@RequestParam("profileId") Long profileId) {

        // 공유 그룹에서 프로필 조회
        Profile profile = shareGroupService.findProfile(profileId);

        return ResultResponse.of(ProfileResultCode.PROFILE_INFO,
                profileConverter.toProfileInfo(profile));
    }

    @GetMapping("/my")
    @Operation(summary = "내 프로필 조회 API", description = "특정 공유그룹에 속한 내 프로필을 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "shareGroupId", description = "조회할 프로필이 속한 공유그룹의 id를 입력해 주세요.")
    })
    public ResultResponse<ProfileResponse.ProfileInfo> getMyProfileInfo(@RequestParam("shareGroupId") Long shareGroupId,
                                                                        @LoginMember Member member) {

        // 해당 공유 그룹에서 내 프로필 조회
        Profile profile = shareGroupService.findProfile(shareGroupId, member.getId());

        // 조회한 프로필을 ProfileInfo로 변환하여 반환
        return ResultResponse.of(ProfileResultCode.PROFILE_INFO,
                profileConverter.toProfileInfo(profile));
    }

    @GetMapping("/profileListInfo")
    @Operation(summary = "프로필 리스트 조회 API", description = "특정 공유그룹의 프로필 리스트를 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "shareGroupId", description = "조회할 프로필이 속한 공유그룹의 id를 입력해 주세요.")
    })
    public ResultResponse<List<ProfileResponse.ProfileInfo>> getProfileList(@RequestParam("shareGroupId") Long shareGroupId) {

        // 공유그룹에 속한 프로필 리스트 조회
        List<Profile> profiles = shareGroupService.findProfileListByShareGroupId(shareGroupId);

        // 조회한 프로필 리스트를 ProfileInfo 형태의 리스트로 변환하여 반환
        List<ProfileResponse.ProfileInfo> profileInfos = profiles
                .stream()
                .map(profileConverter::toProfileInfo)
                .toList();

        return ResultResponse.of(ProfileResultCode.PROFILE_INFO_LIST, profileInfos);
    }



    // 다이어리에 태그된 프로필 조회 API
    @GetMapping("/diaries/{diaryId}/tagged")
    @Operation(summary = "태그된 프로필 조회 API", description = "특정 다이어리에 태그된 프로필 리스트를 조회하는 API입니다.")
    public ResultResponse<ProfileResponse.TaggedProfileList> getTaggedProfiles(@PathVariable Long diaryId) {

        // 다이어리 ID를 통해 태그된 프로필 리스트를 조회하고, 컨버터로 변환
        List<Profile> taggedProfileList = diaryService.getTaggedProfilesList(diaryId);

        // 변환된 프로필 정보 반환
        return ResultResponse.of(TAGGED_MEMBERS_LIST,
                profileConverter.toTaggedProfileList(diaryId, taggedProfileList));
    }
}
