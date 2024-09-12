package ssu.today.domain.shareGroup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.member.repository.MemberRepository;
import ssu.today.domain.shareGroup.converter.ShareGroupConverter;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.Role;
import ssu.today.domain.shareGroup.entity.ShareGroup;
import ssu.today.domain.shareGroup.entity.Status;
import ssu.today.domain.shareGroup.repository.ProfileRepository;
import ssu.today.domain.shareGroup.repository.ShareGroupRepository;
import ssu.today.global.error.BusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ssu.today.domain.shareGroup.entity.Status.ACTIVE;
import static ssu.today.domain.shareGroup.entity.Status.PENDING;
import static ssu.today.global.error.code.JwtErrorCode.MEMBER_NOT_FOUND;
import static ssu.today.global.error.code.ShareGroupErrorCode.ALREADY_JOINED;
import static ssu.today.global.error.code.ShareGroupErrorCode.MEMBER_COUNT_ERROR;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_ALREADY_STARTED;
import static ssu.today.global.error.code.ShareGroupErrorCode.SHARE_GROUP_CREATOR_NOT_FOUND;
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
                .nickName(member.getNickName())   // 그룹 내 닉네임 (일단 계정의 닉네임)
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

        // 2. PENDING 상태가 아닌 경우 참여 불가
        if (shareGroup.getStatus() != Status.PENDING) {
            throw new BusinessException(SHARE_GROUP_ALREADY_STARTED);
        }

        // 3. 이미 그룹에 참여 중인 멤버인지 확인 (중복 가입 방지)
        boolean alreadyJoined = profileRepository.existsByShareGroupIdAndMemberId(shareGroupId, member.getId());
        if (alreadyJoined) {
            throw new BusinessException(ALREADY_JOINED);
        }

        // 4. 가입하지 않은 사용자라면, 새로운 Profile 생성
        Profile profile = Profile.builder()
                .nickName(member.getNickName())   // 계정 닉네임 가져오기
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

    /**
     * 매시간 실행되는 스케줄링 작업: 24시간이 지난 PENDING 상태의 그룹을 ACTIVE로 변경하고 초대 코드를 무효화
     * */
    @Scheduled(cron = "30 0 0 * * *") // 매일 자정(00시) 0분 30초마다 스케줄러 실행
    @Transactional
    public void updatePendingGroups() {

        // 현재 시간 계산
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간보다 openAt이 지난 PENDING 상태의 그룹들만 조회
        List<ShareGroup> pendingGroups = shareGroupRepository.findAllByStatusAndOpenAtBefore(PENDING, now);

        // 조회된 각 그룹에 대해 상태를 ACTIVE로 변경하고 초대 코드를 무효화시킴
        for (ShareGroup shareGroup : pendingGroups) {
            updateGroupStatus(shareGroup);
        }
    }

    @Transactional
    public void updateGroupStatus(ShareGroup shareGroup) {
        shareGroup.setStatus(ACTIVE);  // 상태를 ACTIVE로 변경
        shareGroup.setInviteCode("NULL");       // 초대 코드를 무효화
        shareGroupRepository.save(shareGroup); // 변경된 정보를 데이터베이스에 저장
        shareGroupRepository.flush(); // 강제로 변경사항 커밋 ..
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
        String ownerName = creatorProfile.getNickName();
        shareGroup.setOwnerName(ownerName);

        return shareGroup;
    }

    @Override
    public ShareGroup findShareGroup(Long shareGroupId) {
        return shareGroupRepository.findById(shareGroupId)
                .orElseThrow(() -> new BusinessException(SHARE_GROUP_NOT_FOUND));
    }
}