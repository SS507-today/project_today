package ssu.today.domain.diary.converter;

import org.springframework.stereotype.Component;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.shareGroup.entity.Profile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiaryConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DiaryResponse.DiaryBundleInfo toDiaryBundleInfo(Long bundleId, Long shareGroupId, List<Diary> diaries) {

        // 다이어리 리스트를 DiaryInfo 리스트로 변환
        List<DiaryResponse.DiaryInfo> diaryInfoList = diaries.stream()
                .map(this::toDiaryInfo)
                .collect(Collectors.toList());

        // DiaryBundleInfo 생성
        return DiaryResponse.DiaryBundleInfo.builder()
                .bundleId(bundleId)
                .shareGroupId(shareGroupId)
                .diaries(diaryInfoList)
                .build();
    }

    public DiaryResponse.DiaryInfo toDiaryInfo(Diary diary) {


        Profile profile = diary.getProfile();
        return DiaryResponse.DiaryInfo.builder()
                .diaryId(diary.getId())
                .writerProfileId(profile.getId())
                .writerProfileImage(profile.getImage())
                .writerNickname(profile.getProfileNickName())
                .writerDescription(profile.getDescription())
                .finalDiaryImage(diary.getFinalDiaryImage())
                // createdAt을 yyyy-MM-dd 형식으로 변환하여 전달
                .createdAt(diary.getCreatedAt().format(DATE_FORMATTER))
                .build();
    }
}
