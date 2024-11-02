package ssu.today.domain.shareGroup.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShareGroupConverter {

    private static final String BASE_URL = "https://today/invite/"; //baseUrl 상수

    // 그룹 생성 시 반환하는 정보 DTO
    public ShareGroupResponse.InviteInfo toShareGroupInviteInfo(ShareGroup shareGroup) {
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

    public ShareGroupResponse.ShareGroupInfo toShareGroupInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.ShareGroupInfo.builder()
                .shareGroupId(shareGroup.getId())
                .ownerName(shareGroup.getOwnerName())  // 생성자 닉네임
                .memberCount(shareGroup.getMemberCount())
                .groupName(shareGroup.getName())
                .image(shareGroup.getCoverImage())  // 이미지 URL
                .description(shareGroup.getDescription())
                .createdAt(shareGroup.getCreatedAt())
                .build();
    }

    public ShareGroupResponse.ShareGroupDetailInfo toShareGroupDetailInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.ShareGroupDetailInfo.builder()
                .shareGroupId(shareGroup.getId())
                .groupName(shareGroup.getName())
                .coverImage(shareGroup.getCoverImage())  // 이미지 URL
                .description(shareGroup.getDescription())
                .ruleFirst(shareGroup.getRuleFirst())
                .ruleSecond(shareGroup.getRuleSecond())
                .ruleThird(shareGroup.getRuleThird())
                .build();
    }


    public ShareGroupResponse.StatusInfo toShareGroupStatusInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.StatusInfo.builder()
                .shareGroupId(shareGroup.getId())
                .status(shareGroup.getStatus())
                .openAt(shareGroup.getOpenAt())
                .build();
    }

    public ShareGroupResponse.JoinInfo toShareGroupJoinInfo(Profile profile) {

        return ShareGroupResponse.JoinInfo.builder()
                .shareGroupId(profile.getShareGroup().getId())
                .profileId(profile.getId())
                .status(profile.getShareGroup().getStatus())
                .joinedAt(profile.getJoinedAt())
                .build();
    }

    public ShareGroupResponse.ShareGroupSimpleInfo toShareGroupSimpleInfo(ShareGroup shareGroup) {

        return ShareGroupResponse.ShareGroupSimpleInfo.builder()
                .shareGroupId(shareGroup.getId())
                .GroupName(shareGroup.getName())
                .coverImage(shareGroup.getCoverImage())
                .status(shareGroup.getStatus())
                .createdAt(shareGroup.getCreatedAt())
                .build();
    }

    // 공유 그룹 목록 반환 DTO
    public ShareGroupResponse.PagedShareGroupInfo toPagedShareGroupInfo(Page<ShareGroup> shareGroupList) {

        // 각 공유 그룹에 대한 상세 정보를 가져오기 (DetailInfo response 재사용)
        List<ShareGroupResponse.ShareGroupSimpleInfo> shareGroupInfoList = shareGroupList
                .stream()
                .map(this::toShareGroupSimpleInfo)
                .toList();

        return ShareGroupResponse.PagedShareGroupInfo.builder()
                .shareGroupInfoList(shareGroupInfoList) // 만든 info 리스트
                .page(shareGroupList.getNumber())
                .totalElements(shareGroupList.getTotalElements())
                .isFirst(shareGroupList.isFirst())
                .isLast(shareGroupList.isLast())
                .build();
    }

    // --- 내 차례일 때 홈 정보 응답 변환
    public ShareGroupResponse.ShareGroupHomeInfo toMyTurnInfo(ShareGroup shareGroup) {
        return ShareGroupResponse.ShareGroupHomeInfo.builder()
                .isMyTurn(true)
                .shareGroupDetailInfo(toShareGroupDetailInfo(shareGroup)) //컨버터 재사용
                .build();
    }

    // --- 내 차례가 아닐 때의 응답 변환
    public ShareGroupResponse.ShareGroupHomeInfo toOtherTurnInfo(Profile currentWriter) {
        return ShareGroupResponse.ShareGroupHomeInfo
                .builder()
                .isMyTurn(false)
                .currentWriter(ShareGroupResponse.CurrentWriter
                        .builder()
                        .profileId(currentWriter.getId())
                        .name(currentWriter.getProfileNickName())
                        .description(currentWriter.getDescription())
                        .image(currentWriter.getImage())
                        .build())
                .build();
    }

    // 그룹 Id만 반환 ( 그룹 삭제 시 반환하는 DTO)
    public ShareGroupResponse.ShareGroupId toShareGroupId(ShareGroup shareGroup) {
        return ShareGroupResponse.ShareGroupId.builder()
                .shareGroupId(shareGroup.getId())
                .build();
    }
}
