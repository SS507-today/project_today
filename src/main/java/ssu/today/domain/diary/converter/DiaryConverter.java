package ssu.today.domain.diary.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.shareGroup.entity.Profile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiaryConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DiaryResponse.DiaryListInfo toDiaryBundleInfo(Long bundleId, Long shareGroupId, List<Diary> diaries) {

        // 다이어리 리스트를 DiaryInfo 리스트로 변환
        List<DiaryResponse.DiaryInfo> diaryInfoList = diaries.stream()
                .map(this::toDiaryInfo)
                .collect(Collectors.toList());

        // DiaryBundleInfo 생성
        return DiaryResponse.DiaryListInfo.builder()
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

    // 단일 번들 정보 변환 메서드
    public DiaryResponse.DiaryBundleInfo toDiaryBundleInfo(DiaryBundle diaryBundle) {
        return DiaryResponse.DiaryBundleInfo.builder()
                .bundleId(diaryBundle.getId())
                .startDate(diaryBundle.getStartedAt().format(DATE_FORMATTER))
                .endDate(diaryBundle.getEndedAt().format(DATE_FORMATTER))
                .build();
    }


    // 번들 목록 페이징 응답 변환 메서드
    public DiaryResponse.PagedDiaryBundleInfo toPagedDiaryBundleInfo(Long shareGroupId, int coverImage, Page<DiaryBundle> bundlePage) {

        //번들 리스트
        List<DiaryResponse.DiaryBundleInfo> bundleInfoList = bundlePage.getContent().stream()
                .map(this::toDiaryBundleInfo)
                .collect(Collectors.toList());

        return DiaryResponse.PagedDiaryBundleInfo.builder()
                .shareGroupId(shareGroupId)
                .coverImage(coverImage) // 그룹 커버 이미지
                .bundleInfoList(bundleInfoList)
                .page(bundlePage.getNumber())
                .totalElements(bundlePage.getTotalElements())
                .isFirst(bundlePage.isFirst())
                .isLast(bundlePage.isLast())
                .build();
    }
}
