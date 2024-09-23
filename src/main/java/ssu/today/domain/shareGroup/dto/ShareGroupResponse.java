package ssu.today.domain.shareGroup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.today.domain.shareGroup.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ShareGroupResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteInfo {
        private Long shareGroupId;
        private String inviteCode;
        private String inviteUrl;
        private Status status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime openAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareGroupInfo {
        private Long shareGroupId;
        private String ownerName;  // 공유그룹 생성자 닉네임
        private int memberCount;   // 그룹 인원
        private String groupName;  // 그룹 이름
        private int image;      // 공유그룹 대표 이미지 URL
        private String description; // 공유그룹 소개
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime createdAt;  // 생성 날짜
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareGroupDetailInfo {
        private Long shareGroupId;
        private String groupName;
        private int coverImage;
        private String description;
        private String ruleFirst;
        private String ruleSecond;
        private String ruleThird;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusInfo {
        private Long shareGroupId;
        private Status status;
        private LocalDateTime openAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinInfo {
        private Long shareGroupId;
        private Long profileId;
        private Status status;
        private LocalDateTime joinedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareGroupSimpleInfo {
        private Long shareGroupId;
        private String GroupName;
        private int coverImage;
        private Status status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedShareGroupInfo {
        private List<ShareGroupSimpleInfo> shareGroupInfoList; //공유그룹 상세 정보 리스트
        private int page; // 페이지 번호
        private long totalElements; // 해당 조건에 부합하는 요소의 총 개수
        private boolean isFirst; // 첫 페이지 여부
        private boolean isLast; // 마지막 페이지 여부
    }

    // 내 차례일 때와 아닐 때의 구조를 하나의 공통된 응답 구조로 만듦.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareGroupHomeInfo {
        private boolean isMyTurn;
        private ShareGroupDetailInfo shareGroupDetailInfo;  // 내 차례일 때 사용
        private CurrentWriter currentWriter;    // 내 차례가 아닐 때 사용
    }

    // 현재 작성자 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWriter {
        private Long profileId;
        private String name;
        private String description;
        private String image;
    }

}