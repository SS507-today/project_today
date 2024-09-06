package ssu.today.domain.shareGroup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.converter.ShareGroupConverter;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
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
    public ResultResponse<ShareGroupResponse.InviteInfo> createShareGroup(@Valid @RequestBody ShareGroupRequest.createShareGroupRequest request,
                                                                          @LoginMember Member member) {
        ShareGroup shareGroup = shareGroupService.createShareGroup(request, member);
        return ResultResponse.of(ShareGroupResultCode.CREATE_SHARE_GROUP,
                shareGroupConverter.toShareGroupInfo(shareGroup));
    }
}
