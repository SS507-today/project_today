package ssu.today.domain.shareGroup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.today.domain.shareGroup.entity.Status;

import java.time.LocalDateTime;

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
    public static class ShareGroupDetailInfo {
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

}