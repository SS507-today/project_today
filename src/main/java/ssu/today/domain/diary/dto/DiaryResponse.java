package ssu.today.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public abstract class DiaryResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreSignedUrlInfo {
        private String preSignedUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadInfo {
        private Long shareGroupId;  // 공유 그룹 ID
        private Long diaryId;
        private Long bundleId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryBundleInfo {
        private Long shareGroupId;
        private Long bundleId;
        private List<DiaryInfo> diaries;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryInfo {
        private Long diaryId;
        private Long writerProfileId;
        private String writerProfileImage;
        private String writerNickname;
        private String writerDescription;
        private String finalDiaryImage;
        private String createdAt;
    }
}
