package ssu.today.domain.diary.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.diary.dto.DiaryRequest;
import ssu.today.domain.diary.dto.DiaryResponse;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.diary.entity.DiaryTag;
import ssu.today.domain.diary.repository.DiaryBundleRepository;
import ssu.today.domain.diary.repository.DiaryRepository;
import ssu.today.domain.diary.repository.DiaryTagRepository;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.repository.ProfileRepository;
import ssu.today.domain.shareGroup.service.ShareGroupService;
import ssu.today.global.error.BusinessException;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static ssu.today.global.error.code.DiaryErrorCode.DIARY_BUNDLE_NOT_FOUND;
import static ssu.today.global.error.code.DiaryErrorCode.DIARY_NOT_FOUND;
import static ssu.today.global.error.code.DiaryErrorCode.INVALID_TAG_PROFILE;
import static ssu.today.global.error.code.ShareGroupErrorCode.PROFILE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final DiaryRepository diaryRepository;
    private final ProfileRepository profileRepository;
    private final DiaryBundleRepository diaryBundleRepository;
    private final DiaryTagRepository diaryTagRepository;
    private final ShareGroupService shareGroupService;

    // 파일 업로드용 임시 url인 presigned url을 생성해서 프론트한테 반환
    @Override
    public Map<String, String> getPresignedUrl(String prefix, String fileName) {
        if (!prefix.isEmpty()) {
            fileName = createPath(prefix, fileName);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return Map.of("preSignedUrl", url.toString());
    }

    // 프론트가 업로드한 이미지의 s3 주소를 DB에 저장
    @Override
    public DiaryResponse.UploadInfo uploadDiary(DiaryRequest.DiaryUploadRequest request, Member member) {
        // 0. 공유그룹 active 상태인지 검증
        shareGroupService.validateShareGroupActive(request.getShareGroupId());

        // 1. 다이어리를 작성한 작성자의 프로필 정보 가져오기
        Profile writerProfile = shareGroupService.findProfile(request.getShareGroupId(), member.getId());

        // 2. 가장 최신 DiaryBundle 가져오기
        DiaryBundle latestBundle = findLatestDiaryBundle(request.getShareGroupId());

        // 3. 다이어리 생성 및 저장
        Diary diary = Diary.builder()
                .finalDiaryImage(request.getFinalImageUrl())
                .profile(writerProfile)  // 작성자 프로필 저장
                .diaryBundle(latestBundle)  // 최신 번들에 다이어리 추가
                .build();
        diaryRepository.save(diary);

        // 4. 태그된 프로필 ID 리스트가 비어있지 않으면 처리
        if (request.getTaggedProfileId() != null && !request.getTaggedProfileId().isEmpty()) {

            // 태그한 프로필 리스트
            List<Profile> taggedProfiles = profileRepository.findByIdIn(request.getTaggedProfileId());

            // 태그된 프로필 중에서 하나라도 존재하지 않거나 유효하지 않으면 예외 발생
            if (taggedProfiles.size() != request.getTaggedProfileId().size()) {
                throw new BusinessException(INVALID_TAG_PROFILE);
            }

            // 태그 정보 저장
            for (Profile taggedProfile : taggedProfiles) {
                DiaryTag tag = DiaryTag.builder()
                        .diary(diary)
                        .profile(taggedProfile)
                        .build();
                diaryTagRepository.save(tag);  // 태그 저장
            }
        }

        // 5. 다이어리 업로드 응답 반환
        return DiaryResponse.UploadInfo.builder()
                .shareGroupId(request.getShareGroupId())
                .diaryId(diary.getId())
                .bundleId(latestBundle.getId())  // 최신 번들의 ID 반환
                .createdAt(diary.getCreatedAt())
                .build();
    }

    /**
     * 공유 그룹의 가장 최신 DiaryBundle을 가져오는 함수
     *
     * @param shareGroupId 공유 그룹 ID
     * @return 가장 최신의 DiaryBundle 객체
     */
    private DiaryBundle findLatestDiaryBundle(Long shareGroupId) {
        // 1. ShareGroup 객체를 가져옴
        ShareGroup shareGroup = shareGroupService.findShareGroup(shareGroupId);

        // 2. 가장 최신의 DiaryBundle을 가져옴
        return diaryBundleRepository.findFirstByShareGroupOrderByStartedAtDesc(shareGroup)
                .orElseThrow(() -> new BusinessException(DIARY_BUNDLE_NOT_FOUND)); // 번들이 없으면 예외 발생
    }

    // presigned url 생성 (put 메소드로)
    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    // presigned 유효기간은 2분.
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // UUID로 고유한 파일 id 만들기
    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    // 고유한 파일 id를 써서 또 고유한 path를 만들기
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + "-" + fileName);
    }

    /////////////// 아래로는 조회 부분

    @Override
    @Transactional(readOnly = true)
    public List<Diary> getDiariesByBundle(Long bundleId, Long shareGroupId) {
        // 1. 번들 ID가 유효한지 확인
        if (!diaryBundleRepository.existsById(bundleId)) {
            throw new BusinessException(DIARY_BUNDLE_NOT_FOUND);
        }

        // 특정 번들에 속한 다이어리들을 최신순으로 조회해서 리턴
        return diaryRepository.findAllByDiaryBundle_IdAndDiaryBundle_ShareGroup_IdOrderByCreatedAtDesc(bundleId, shareGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiaryBundle> getDiaryBundleList(Long shareGroupId, Pageable pageable) {

        // 특정 공유 그룹 ID를 통해 다이어리 번들을 페이징하여 가져오기
        return diaryBundleRepository.findByShareGroupId(shareGroupId, pageable);
    }


    @Override
    public List<Profile> getTaggedProfilesList(Long diaryId) {

        // 다이어리 ID로 다이어리 존재 여부 확인
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(DIARY_NOT_FOUND));

        // 다이어리 ID를 통해 태그된 DiaryTag 목록을 조회하고, 해당 프로필을 추출하면서 DB에 존재하는지 검증
        List<Profile> taggedProfiles = diaryTagRepository.findAllByDiaryId(diaryId).stream()
                .map(diaryTag -> profileRepository.findById(diaryTag.getProfile().getId())
                        .orElseThrow(() -> new BusinessException(PROFILE_NOT_FOUND)))  // 프로필이 DB에 존재하는지 확인
                .collect(Collectors.toList());

        // 태그된 프로필 리스트가 비어있다면 null 반환
        return taggedProfiles.isEmpty() ? null : taggedProfiles;
    }
}