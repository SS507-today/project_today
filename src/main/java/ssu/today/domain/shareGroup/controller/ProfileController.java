package ssu.today.domain.shareGroup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.shareGroup.converter.ProfileConverter;
import ssu.today.domain.shareGroup.dto.ProfileResponse;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.service.ShareGroupService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.ProfileResultCode;
import ssu.today.global.result.code.ShareGroupResultCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@Tag(name = "03. 프로필 관련 API", description = "프로필 조회, 수정 등의 작업을 처리하는 API입니다.")
public class ProfileController {

    private final ShareGroupService shareGroupService;
    private final ProfileConverter profileConverter;

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
}
