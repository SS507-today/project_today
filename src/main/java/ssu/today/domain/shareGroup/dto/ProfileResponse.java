package ssu.today.domain.shareGroup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.today.domain.shareGroup.entity.Role;
import ssu.today.domain.shareGroup.entity.Status;

import java.time.LocalDateTime;

public abstract class ProfileResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfo {
        private Long shareGroupId;
        private Long profileId;
        private String profileNickName;
        private String description;
        private String image;
        private Role role;
        private boolean isMyTurn;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime joinedAt;
    }
}
