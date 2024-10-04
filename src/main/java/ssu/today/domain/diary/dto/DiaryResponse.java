package ssu.today.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    public static class DiaryListInfo {
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


    @Getter
    @Builder
    public static class PagedDiaryBundleInfo {
        private Long shareGroupId;
        private List<DiaryBundleInfo> bundleInfoList;
        private int coverImage; //커버 이미지
        private int page; // 페이지 번호
        private long totalElements; // 해당 조건에 부합하는 요소의 총 개수
        private boolean isFirst; // 첫 페이지 여부
        private boolean isLast; // 마지막 페이지 여부
    }

    @Getter
    @Builder
    public static class DiaryBundleInfo {
        private Long bundleId;
        private String startDate;
        private String endDate;
    }
}
