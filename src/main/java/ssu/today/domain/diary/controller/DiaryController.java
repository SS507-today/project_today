package ssu.today.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.diary.converter.DiaryConverter;
import ssu.today.domain.diary.dto.DiaryRequest;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.service.DiaryService;
import ssu.today.domain.member.entity.Member;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.DiaryResultCode;
import ssu.today.global.security.annotation.LoginMember;

import java.util.Map;

import static ssu.today.global.result.code.DiaryResultCode.CREATE_PRESIGNED_URL;

@RestController
@RequestMapping("/bundles")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "04. 다이어리 관련 API", description = "다이어리 업로드, 조회 등을 수행하는 API입니다.")
public class DiaryController {

    private final DiaryService diaryService;

    // Presigned URL 요청 API
    @PostMapping("/preSignedUrl")
    @Operation(summary = "Presigned URL 요청 API", description = "S3에 이미지를 업로드하기 위한 Presigned URL을 요청합니다.")
    public ResultResponse<Map<String, String>> getPresignedUrl(@RequestBody DiaryRequest.PresignedUrlRequest request) {
        Map<String, String> preSignedUrl = diaryService.getPresignedUrl("image", request.getImageName());
        return ResultResponse.of(CREATE_PRESIGNED_URL, preSignedUrl);
    }

    // 다이어리 업로드 API
    @PostMapping("/upload")
    @Operation(summary = "다이어리 업로드 API", description = "S3에 업로드 완료된 사진을 서버에 다이어리로 저장합니다.")
    public ResultResponse<DiaryResponse.UploadInfo> uploadDiary(@Valid @RequestBody DiaryRequest.DiaryUploadRequest request,
                                                                @LoginMember Member member) {
        DiaryResponse.UploadInfo response = diaryService.uploadDiary(request, member);
        return ResultResponse.of(DiaryResultCode.UPLOAD_DIARY, response);
    }

    

}
