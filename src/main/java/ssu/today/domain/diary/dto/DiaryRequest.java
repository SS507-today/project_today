package ssu.today.domain.diary.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class DiaryRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignedUrlRequest {
        @NotEmpty(message = "누락될 수 없습니다. 확장자까지 함께 입력해주세요")
        private String imageName;  // Presigned URL 요청 시 이미지 이름
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryUploadRequest {
        private Long shareGroupId;
        @NotEmpty(message = "이미지는 누락될 수 없습니다.")
        private String finalImageUrl;         // 최종 일기 이미지 URL
        private List<Long> taggedProfileId;   // 태그된 프로필 ID 리스트
    }

}
