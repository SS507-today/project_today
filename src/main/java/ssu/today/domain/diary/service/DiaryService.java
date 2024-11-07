package ssu.today.domain.diary.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssu.today.domain.diary.dto.DiaryRequest;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.entity.Profile;

import java.util.List;
import java.util.Map;

public interface DiaryService {
    Map<String, String> getPresignedUrl(String prefix, String fileName);
    DiaryResponse.UploadInfo uploadDiary(DiaryRequest.DiaryUploadRequest request, Member member);
    List<Diary> getDiariesByBundle(Long shareGroupId, Long bundleId);
    Page<DiaryBundle> getDiaryBundleList(Long shareGroupId, Pageable pageable);
    List<Profile> getTaggedProfilesList(Long diaryId);
    DiaryBundle findLatestDiaryBundle(Long shareGroupId);
}
