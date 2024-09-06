package ssu.today.domain.shareGroup.converter;

import org.springframework.stereotype.Component;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;

@Component
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
}
