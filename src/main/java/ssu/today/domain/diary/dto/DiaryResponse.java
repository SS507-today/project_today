package ssu.today.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
