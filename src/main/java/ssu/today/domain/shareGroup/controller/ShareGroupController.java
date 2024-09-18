package ssu.today.domain.shareGroup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.converter.ShareGroupConverter;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.service.ShareGroupService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.ShareGroupResultCode;
import ssu.today.global.security.annotation.LoginMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shareGroups")
@Tag(name = "02. 공유그룹 관련 API", description = "공유그룹 생성, 참여, 조회, 삭제 등을 처리하는 API입니다.")
public class ShareGroupController {
    private final ShareGroupConverter shareGroupConverter;
    private final ShareGroupService shareGroupService;

    @PostMapping
    @Operation(summary = "공유그룹 생성 API", description = "새로운 공유그룹을 생성하는 API입니다.")
    public ResultResponse<ShareGroupResponse.InviteInfo> createShareGroup(@RequestBody @Valid ShareGroupRequest.createShareGroupRequest request,
                                                                          @LoginMember Member member) {
        ShareGroup shareGroup = shareGroupService.createShareGroup(request, member);
        return ResultResponse.of(ShareGroupResultCode.CREATE_SHARE_GROUP,
                shareGroupConverter.toShareGroupInviteInfo(shareGroup));
    }

    @GetMapping
    @Operation(summary = "초대 코드로 공유그룹 조회 API", description = "inviteCode로 특정 공유그룹을 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "inviteCode", description = "참여하려는 공유그룹의 초대 코드")
    })
    public ResultResponse<ShareGroupResponse.ShareGroupInfo> getShareGroupByInviteCode(@RequestParam String inviteCode) {

        // 만약 inviteCode가 URL 형태라면, 마지막 슬래시 이후의 부분만 추출
        if (inviteCode.contains("/")) {
            inviteCode = inviteCode.substring(inviteCode.lastIndexOf("/") + 1);
        }

        // 초대 코드로 공유그룹 조회
        ShareGroup shareGroup = shareGroupService.findShareGroup(inviteCode);

        return ResultResponse.of(ShareGroupResultCode.SHARE_GROUP_INFO,
                shareGroupConverter.toShareGroupInfo(shareGroup));
    }

    @GetMapping("{shareGroupId}/status")
    @Operation(summary = "공유그룹 상태 조회 API", description = "shareGroupId로 공유그룹 상태를 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "shareGroupId", description = "특정 공유그룹 id를 입력해 주세요.")
    })
    public ResultResponse<ShareGroupResponse.StatusInfo> getStatusInfo(@PathVariable(name = "shareGroupId") Long shareGroupId) {

        ShareGroup shareGroup = shareGroupService.findShareGroup(shareGroupId);

        return ResultResponse.of(ShareGroupResultCode.SHARE_GROUP_INFO,
                shareGroupConverter.toShareGroupStatusInfo(shareGroup));
    }

    @PostMapping("{shareGroupId}/join")
    @Operation(summary = "공유그룹 참여 API", description = "특정 공유그룹에 참여하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "shareGroupId", description = "특정 공유그룹 id를 입력해 주세요.")
    })
    public ResultResponse<ShareGroupResponse.JoinInfo> joinShareGroup(@PathVariable(name = "shareGroupId") Long shareGroupId,
                                                                      @LoginMember Member member) {
        // 그룹 id와 member정보를 바탕으로 프로필 생성
        Profile profile = shareGroupService.joinShareGroup(shareGroupId, member);
        return ResultResponse.of(ShareGroupResultCode.JOIN_SHARE_GROUP,
                shareGroupConverter.toShareGroupJoinInfo(profile));
    }

    @GetMapping("/{shareGroupId}")
    @Operation(summary = "공유그룹 조회 API", description = "shareGroupId로 특정 공유그룹을 조회하는 API입니다.")
    @Parameters(value = {
            @Parameter(name = "shareGroupId", description = "특정 공유그룹 id를 입력해 주세요.")
    })
    public ResultResponse<ShareGroupResponse.ShareGroupDetailInfo> getShareGroupDetailInfo(@PathVariable(name = "shareGroupId") Long shareGroupId) {
        // 공유 그룹 상태 검증, active가 아니면 조회되지 않음
        shareGroupService.validateShareGroupActive(shareGroupId);

        ShareGroup shareGroup = shareGroupService.findShareGroup(shareGroupId);

        return ResultResponse.of(ShareGroupResultCode.SHARE_GROUP_INFO,
                shareGroupConverter.toShareGroupDetailInfo(shareGroup));
    }
}