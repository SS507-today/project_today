package ssu.today.domain.shareGroup.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class ShareGroupRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createShareGroupRequest {
        @NotEmpty(message = "공유 그룹 이름은 필수로 입력해야 합니다.")
        @Size(max = 15, message = "그룹명은 최대 15자까지 입력 가능합니다.")
        private String name;
        @Min(2)
        @Max(6)
        @NotNull(message = "멤버의 수는 최소 2명, 최대 6명이어야 합니다.")
        private Integer memberCount;
        @NotEmpty(message = "그룹 소개는 필수로 입력해야 합니다.")
        @Size(max = 50, message = "그룹 소개는 최대 50자까지 입력 가능합니다.")
        private String description;
        @NotNull
        private int coverImage; //프론트에 저장된 커버이미지 숫자
        @Size(max = 30, message = "룰은 최대 30자까지 입력 가능합니다.")
        private String ruleFirst;
        @Size(max = 30, message = "룰은 최대 30자까지 입력 가능합니다.")
        private String ruleSecond;
        @Size(max = 30, message = "룰은 최대 30자까지 입력 가능합니다.")
        private String ruleThird;
    }
}
