package ssu.today.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.today.domain.diary.converter.DiaryConverter;
import ssu.today.domain.diary.dto.DiaryRequest;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.diary.entity.DiaryTag;
import ssu.today.domain.diary.service.DiaryService;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.service.ShareGroupService;
import ssu.today.global.result.ResultResponse;
import ssu.today.global.result.code.DiaryResultCode;
import ssu.today.global.security.annotation.LoginMember;

import java.util.List;
import java.util.Map;

import static ssu.today.global.result.code.DiaryResultCode.CREATE_PRESIGNED_URL;

@RestController
@RequestMapping("/bundles")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "04. 다이어리 관련 API", description = "다이어리 업로드, 조회 등을 수행하는 API입니다.")
public class DiaryController {

    private final DiaryService diaryService;
    private final DiaryConverter diaryConverter;
    private final ShareGroupService shareGroupService;

    // Presigned URL 요청 API
    @PostMapping("/preSignedUrl")
    @Operation(summary = "Presigned URL 요청 API", description = "S3에 이미지를 업로드하기 위한 Presigned URL을 요청합니다.")
    public ResultResponse<Map<String, String>> getPresignedUrl(@Valid @RequestBody DiaryRequest.PresignedUrlRequest request) {
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

    // 특정 번들에 속한 다이어리 목록 조회 API
    @GetMapping("/{bundleId}/diaries")
    @Operation(summary = "특정 번들의 다이어리 목록 조회 API", description = "특정 번들에 속한 다이어리 목록을 최신순으로 조회합니다.")
    public ResultResponse<DiaryResponse.DiaryListInfo> getDiariesByBundle(@PathVariable(name = "bundleId") Long bundleId,
                                                                          @RequestParam Long shareGroupId) {
        // 1. 서비스에서 다이어리 리스트 가져오기
        List<Diary> diaries = diaryService.getDiariesByBundle(bundleId, shareGroupId);

        // 2. 컨버터를 사용하여 다이어리 리스트를 변환
        DiaryResponse.DiaryListInfo response = diaryConverter.toDiaryBundleInfo(bundleId, shareGroupId, diaries);

        // 3. 응답 반환
        return ResultResponse.of(DiaryResultCode.DIARY_LIST, response);
    }

    // 다이어리 번들의 목록 조회 API
    @GetMapping
    @Operation(summary = "다이어리 번들 목록 조회 API", description = "특정 공유 그룹의 다이어리 번들을 페이징하여 최신순으로 조회합니다.")
    @Parameters(value = {
            @Parameter(name = "page", description = "조회할 페이지를 입력해 주세요.(0번부터 시작)"),
            @Parameter(name = "size", description = "한 페이지에 나타낼 공유그룹 개수를 입력해주세요.")
    })
    public ResultResponse<DiaryResponse.PagedDiaryBundleInfo> getDiaryBundleList( @RequestParam Long shareGroupId,
                                                                                  @PageableDefault(size = 10, sort = "startedAt", direction = Sort.Direction.DESC)
                                                                                  @Parameter(hidden = true) Pageable pageable) {
        // 공유 그룹 가져오기
        ShareGroup shareGroup = shareGroupService.findShareGroup(shareGroupId);

        // 번들 목록 조회
        Page<DiaryBundle> bundleList = diaryService.getDiaryBundleList(shareGroupId, pageable);

        return ResultResponse.of(DiaryResultCode.DIARY_BUNDLE_LIST,
                diaryConverter.toPagedDiaryBundleInfo(shareGroupId, shareGroup.getCoverImage(), bundleList));
    }

    // 제일 최신 번들 조회 API
    @GetMapping("/latest")
    @Operation(summary = "제일 최신 번들 조회 API", description = "제일 최신 번들을 조회해요.")
    public ResultResponse<DiaryResponse.DiaryListInfo> getLatestBundle(@RequestParam Long shareGroupId) {

        // 제일 최신 번들 조회
        DiaryBundle bundle = diaryService.findLatestDiaryBundle(shareGroupId);
        Long bundleId = bundle.getId();
        List<Diary> diaries = bundle.getDiaryList();

        // 2. 컨버터를 사용하여 다이어리 리스트를 변환
        DiaryResponse.DiaryListInfo response = diaryConverter.toDiaryBundleInfo(bundleId, shareGroupId, diaries);

        // 3. 응답 반환
        return ResultResponse.of(DiaryResultCode.DIARY_BUNDLE_LIST, response);
    }
}
