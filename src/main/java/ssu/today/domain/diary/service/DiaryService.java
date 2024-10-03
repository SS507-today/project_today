package ssu.today.domain.diary.service;

import ssu.today.domain.diary.dto.DiaryRequest;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.member.entity.Member;

import java.util.Map;

public interface DiaryService {
    Map<String, String> getPresignedUrl(String prefix, String fileName);
    DiaryResponse.UploadInfo uploadDiary(DiaryRequest.DiaryUploadRequest request, Member member);
}
