package ssu.today.domain.shareGroup.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;
import ssu.today.domain.shareGroup.service.ShareGroupService;

@Component
@RequiredArgsConstructor
public class ShareGroupConverter {

    private static final String BASE_URL = "https://today/invite/"; //baseUrl 상수

    // 그룹 생성 시 반환하는 정보 DTO
    public ShareGroupResponse.InviteInfo toShareGroupInfo(ShareGroup shareGroup) {
        return ShareGroupResponse.InviteInfo.builder()
                .shareGroupId(shareGroup.getId())
                .inviteUrl(BASE_URL + shareGroup.getInviteCode())  // 초대 URL 생성
                .inviteCode(shareGroup.getInviteCode())
                .createdAt(shareGroup.getCreatedAt())
                .status(shareGroup.getStatus())
                .openAt(shareGroup.getOpenAt())
                .build();
    }


    public ShareGroup toEntity(ShareGroupRequest.createShareGroupRequest request, String inviteCode) {
        return ShareGroup.builder()
                .name(request.getName())             // 요청에서 받은 그룹명
                .memberCount(request.getMemberCount()) // 요청에서 받은 멤버 수
                .description(request.getDescription()) // 요청에서 받은 그룹 소개
                .coverImage(request.getCoverImage())   // 요청에서 받은 커버 이미지 정보
                .ruleFirst(request.getRuleFirst())     // 요청에서 받은 첫 번째 그룹 룰
                .ruleSecond(request.getRuleSecond())   // 요청에서 받은 두 번째 그룹 룰
                .ruleThird(request.getRuleThird())     // 요청에서 받은 세 번째 그룹 룰
                .inviteCode(inviteCode)                // 생성된 초대 코드
                .status(Status.PENDING)// 초기 상태는 PENDING
                .build();
    }

    public ShareGroupResponse.ShareGroupDetailInfo toShareGroupDetailInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.ShareGroupDetailInfo.builder()
                .shareGroupId(shareGroup.getId())
                .ownerName(shareGroup.getOwnerName())  // 생성자 닉네임
                .memberCount(shareGroup.getMemberCount())
                .groupName(shareGroup.getName())
                .image(shareGroup.getCoverImage())  // 이미지 URL
                .description(shareGroup.getDescription())
                .createdAt(shareGroup.getCreatedAt())
                .build();
    }

    public ShareGroupResponse.StatusInfo toShareGroupStatusInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.StatusInfo.builder()
                .shareGroupId(shareGroup.getId())
                .status(shareGroup.getStatus())
                .openAt(shareGroup.getOpenAt())
                .build();
    }
}
