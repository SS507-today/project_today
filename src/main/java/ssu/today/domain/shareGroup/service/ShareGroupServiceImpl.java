package ssu.today.domain.shareGroup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.domain.shareGroup.converter.ShareGroupConverter;
import ssu.today.domain.shareGroup.dto.ProfileRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.dto.ShareGroupResponse;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.Role;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;
import ssu.today.domain.shareGroup.repository.ProfileRepository;
import ssu.today.domain.shareGroup.repository.ShareGroupRepository;
import ssu.today.global.error.BusinessException;
import ssu.today.global.error.code.ShareGroupErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ssu.today.domain.shareGroup.entity.Status.ACTIVE;
import static ssu.today.domain.shareGroup.entity.Status.PENDING;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;
import static ssu.today.global.error.code.ShareGroupErrorCode.CURRENT_WRITER_NOT_FOUND;
import static ssu.today.global.error.code.ShareGroupErrorCode.MAX_MEMBER_LIMIT_EXCEEDED;
import static ssu.today.global.error.code.ShareGroupErrorCode.MEMBER_COUNT_ERROR;
import static ssu.today.global.error.code.ShareGroupErrorCode.NOT_CREATOR;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_ALREADY_STARTED;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_CREATOR_NOT_FOUND;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_NOT_ACTIVE;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ShareGroupServiceImpl implements ShareGroupService {

    private final ShareGroupConverter shareGroupConverter;
    private final ShareGroupRepository shareGroupRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public ShareGroup createShareGroup(ShareGroupRequest.createShareGroupRequest request,
                                       Member member) {

        // today1. memberCount 검증: 2~6명 사이여야 함
        if (request.getMemberCount() < 2 || request.getMemberCount() > 6) {
            throw new BusinessException(MEMBER_COUNT_ERROR);
        }

        // 2. memberId 존재 여부 검증
        if (!memberRepository.existsById(member.getId())) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        // 초대링크를 위한 고유번호 생성 (UUID)
        String inviteCode = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // 엔티티로 변환하는 로직
        ShareGroup newShareGroup = shareGroupConverter.toEntity(request, inviteCode);
        newShareGroup.setOwnerName(member.getNickName()); //오너 이름 추가

        // 생성된 공유 그룹을 먼저 저장하여 createdAt이 설정되게 함
        newShareGroup = shareGroupRepository.save(newShareGroup);

        // createdAt이 설정된 후 openAt을 계산 ( 24시간 후의 다음 정시 시간)
        LocalDateTime openAt = calculateOpenAt(newShareGroup.getCreatedAt());
        newShareGroup.setOpenAt(openAt);  // 계산된 openAt 값을 엔티티에 설정

        // Profile 테이블에 그룹 생성자를 추가하는 로직
        Profile profile = Profile.builder()
                .profileNickName(member.getNickName())   // 그룹 내 닉네임 (일단 계정의 닉네임)
                .image(member.getImage()) // 그룹 내 이미지 (일단 계정의 이미지)
                .description("")  // 그룹 내 소개 -> 일단 비워둠
                .role(Role.CREATOR)         // 생성자는 creator 역할로 설정
                .isMyTurn(false)          // 처음 생성시 내 차례는 아님
                .shareGroup(newShareGroup)   // 해당 그룹
                .member(member) //멤버에 본인 설정.
                .joinedAt(LocalDateTime.now()) // 그룹에 참가한 시간
                .build();

        // Profile 저장
        profileRepository.save(profile);

        // openAt 값을 레포지토리에 저장
        return shareGroupRepository.save(newShareGroup);
    }

    @Transactional
    @Override
    public Profile joinShareGroup(Long shareGroupId, Member member) {

        // 1. 해당 공유 그룹 조회
        ShareGroup shareGroup = findShareGroup(shareGroupId);

        // 2. PENDING 상태가 아닌 경우 (이미 그룹 시작했으면) 참여 불가
        if (shareGroup.getStatus() != Status.PENDING) {
            throw new BusinessException(SHARE_GROUP_ALREADY_STARTED);
        }

        // 2.5 그룹에 참여한 사람이 6명이 넘었으면 참여 불가
        if (shareGroup.getProfileList().size() > shareGroup.getMemberCount()) {
            throw new BusinessException(MAX_MEMBER_LIMIT_EXCEEDED);
        }

        // 3. 이미 그룹에 참여 중인 멤버인지 확인 (중복 가입 방지)
        if (doesProfileExist(shareGroupId, member.getId())) {
            throw new BusinessException(ShareGroupErrorCode.ALREADY_JOINED);
        }

        // 4. 가입하지 않은 사용자라면, 새로운 Profile 생성
        Profile profile = Profile.builder()
                .profileNickName(member.getNickName())   // 계정 닉네임 가져오기
                .image(member.getImage())         // 계정 이미지 가져오기
                .description("")                  // 그룹 내 소개 (빈 값이 기본)
                .role(Role.PARTICIPANT)           // 공유그룹 오너가 아닌, 일반 멤버 역할로 참여
                .isMyTurn(false)                  // 기본값
                .member(member)                   // 멤버 정보
                .shareGroup(shareGroup)           // 해당 공유 그룹
                .joinedAt(LocalDateTime.now())    // 참여 시간
                .build();

        // 5. Profile 저장
        profileRepository.save(profile);

        return profile;  // 새로 생성된 프로필 반환
    }

    // openAt 계산 로직
    private LocalDateTime calculateOpenAt(LocalDateTime createdAt) {

        // 24시간 후의 시간 계산
        LocalDateTime after24Hours = createdAt.plusHours(24);

        // 그 시간에 해당하는 날짜의 00시로 설정
        LocalDateTime openAt = after24Hours.withHour(0).withMinute(0).withSecond(0).withNano(0);

        // after24Hours가 자정 이후라면, 그날의 자정은 이미 지났기 때문에 하루를 더해 자정을 설정
        if (after24Hours.isAfter(openAt)) {
            openAt = openAt.plusDays(1);
        }

        return openAt;
    }

    @Override
    public Page<ShareGroup> getMyShareGroupList(Member member, Pageable pageable) {
        // 내 멤버id를 통해 내 profile을 가져와서, 해당 profile의 shareGroupId를 추출
        // 이때 삭제되지 않은 프로필만 가져오도록 !!
        List<Long> shareGroupIdList = profileRepository.findByMemberId(member.getId())
                .stream()
                .filter(profile -> profile.getDeletedAt() == null) // 논리 삭제되지 않은 프로필만 필터링
                .map(profile -> profile.getShareGroup().getId())
                .collect(Collectors.toList()); // 리스트로 수집

        // 추출한 공유 그룹 ID 리스트를 통해 해당 공유 그룹들을 조회 + pending 인것도 조회.
        // 페이징 처리하여 가져옴
        return shareGroupRepository.findByIdIn(shareGroupIdList, pageable);
    }

    @Override
    public ShareGroupResponse.ShareGroupHomeInfo getShareGroupHome(Long shareGroupId, Member member) {
        // 1. 그룹 상태 검증, active가 아니면 에러
        validateShareGroupActive(shareGroupId);

        // 2. shareGroup 가져오기
        ShareGroup shareGroup = findShareGroup(shareGroupId);

        // 3. 해당 멤버의 프로필 조회 (isMyTurn 필드 확인 위함)
        Profile myProfile = findProfile(shareGroupId, member.getId());

        if (Boolean.TRUE.equals(myProfile.getIsMyTurn())) {
            // 내 차례일 때: 컨버터를 사용해 변환
            return shareGroupConverter.toMyTurnInfo(shareGroup);

        } else {
            // 내 차례가 아닐 때: 현재 작성자를 조회하고, 컨버터를 사용해 변환
            // isMyTurn이 true인 프로필을 찾아 저장
            Profile currentWriter = shareGroup.getProfileList().stream()
                    .filter(Profile::getIsMyTurn)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(CURRENT_WRITER_NOT_FOUND));

            return shareGroupConverter.toOtherTurnInfo(currentWriter);
        }
    }

    @Override
    public ShareGroup findShareGroup(String inviteCode) {
        // 1. 초대 코드로 공유 그룹 조회
        ShareGroup shareGroup = shareGroupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(SHARE_GROUP_NOT_FOUND));  // 그룹이 없으면 예외 발생

        // 2. 그룹의 상태가 PENDING인지 확인
        if (shareGroup.getStatus() != PENDING) {
            throw new BusinessException(SHARE_GROUP_ALREADY_STARTED);  // 그룹 상태가 PENDING이 아니면 예외 발생
        }

        // 3. 공유그룹 오너의 이름을 확인
        Profile creatorProfile = profileRepository.findByShareGroupAndRole(shareGroup, Role.CREATOR)
                .orElseThrow(() -> new BusinessException(SHARE_GROUP_CREATOR_NOT_FOUND));  // 생성자가 없으면 예외 발생
        String ownerName = creatorProfile.getProfileNickName();
        shareGroup.setOwnerName(ownerName);

        return shareGroup;
    }

    @Override
    public ShareGroup findShareGroup(Long shareGroupId) {
        return shareGroupRepository.findById(shareGroupId)
                .orElseThrow(() -> new BusinessException(SHARE_GROUP_NOT_FOUND));
    }

    // 공유 그룹이 ACTIVE 상태인지 검증하는 메서드
    @Override
    public void validateShareGroupActive(Long shareGroupId) {
        ShareGroup shareGroup = findShareGroup(shareGroupId); // 공유 그룹 조회
        if (shareGroup.getStatus() != Status.ACTIVE) {
            throw new BusinessException(SHARE_GROUP_NOT_ACTIVE); // 상태가 ACTIVE가 아닐 경우 예외 발생
        }
    }

    @Override
    public Profile findProfile(Long shareGroupId, Long memberId) {
        //공유그룹 존재하는지 검증
        findShareGroup(shareGroupId);

        return profileRepository.findByShareGroupIdAndMemberId(shareGroupId, memberId)
                .orElseThrow(() -> new BusinessException(ShareGroupErrorCode.PROFILE_NOT_FOUND));
    }

    @Override
    public Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(ShareGroupErrorCode.PROFILE_NOT_FOUND));
    }

    @Override
    public List<Profile> findProfileListByShareGroupId(Long shareGroupId) {
        //공유그룹 존재하는지 검증
        findShareGroup(shareGroupId);

        return profileRepository.findByShareGroupId(shareGroupId);
    }

    @Override
    public boolean doesProfileExist(Long shareGroupId, Long memberId) {
        return profileRepository.existsByShareGroupIdAndMemberId(shareGroupId, memberId);
    }

    @Override
    @Transactional
    public ShareGroup leaveShareGroup(Long shareGroupId, Member member) {

        // 1. 공유 그룹이 존재하는지 검증
        ShareGroup shareGroup = findShareGroup(shareGroupId);

        // 2. 멤버가 해당 공유 그룹에 속한 프로필인지 확인
        Profile profile = findProfile(shareGroupId, member.getId());

        // 3. 해당 프로필 삭제 (논리적 삭제, deletedAt 설정)
        profile.delete();

        // 4. 삭제된 프로필을 저장
        profileRepository.save(profile);

        // 5. 공유 그룹 리턴
        return shareGroup;
    }

    @Transactional
    @Override
    public Profile updateProfile(Long profileId, ProfileRequest.UpdateProfile request) {

        Profile myProfile = findProfile(profileId);

        myProfile.setProfileNickName(request.getNickName());
        myProfile.setDescription(request.getDescription());
        myProfile.setUpdatedAt(LocalDateTime.now());

        return profileRepository.save(myProfile);
    }

    @Transactional
    @Override
    public ShareGroup deleteShareGroup(Long shareGroupId, Member member) {

        // 1. 공유 그룹이 존재하는지 검증
        ShareGroup shareGroup = findShareGroup(shareGroupId);

        // 2. 멤버가 해당 공유 그룹에 속한 프로필인지 확인
        Profile profile = findProfile(shareGroupId, member.getId());

        // 3. CREATOR 권한 확인
        if (profile.getRole() != Role.CREATOR) {
            throw new BusinessException(NOT_CREATOR);
        }

        // 4. 해당 공유그룹 삭제
        shareGroup.delete();

        // 5. 공유 그룹 리턴
        return shareGroup;
    }

    // ACTIVE 상태의 공유 그룹 리스트 반환
    @Override
    public List<ShareGroup> getActiveShareGroups() {
        return shareGroupRepository.findByStatus(ACTIVE);
    }

}