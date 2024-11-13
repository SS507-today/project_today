package ssu.today.domain.shareGroup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.today.domain.shareGroup.entity.Role;

import java.time.LocalDateTime;
import java.util.List;

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

    @Getter
    @Builder
    public static class TaggedProfile {
        private Long profileId;
        private String name;
        private String image;
    }

    @Getter
    @Builder
    public static class TaggedProfileList {
        private Long diaryId;
        private List<TaggedProfile> taggedMembersList;
    }

    @Getter
    @Builder
    public static class UpdateProfile {
        private Long shareGroupId;
        private Long profileId;
        private LocalDateTime updatedAt;
    }
}
