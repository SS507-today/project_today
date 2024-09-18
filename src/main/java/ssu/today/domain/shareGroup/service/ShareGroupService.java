package ssu.today.domain.shareGroup.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.domain.shareGroup.entity.ShareGroup;

public interface ShareGroupService {
    ShareGroup createShareGroup(ShareGroupRequest.createShareGroupRequest request,
                                Member member);
    Profile joinShareGroup(Long shareGroupId, Member member);
    ShareGroup findShareGroup(String inviteCode);
    ShareGroup findShareGroup(Long shareGroupId);
    void validateShareGroupActive(Long shareGroupId);
    Page<ShareGroup> getMyShareGroupList(Member member, Pageable pageable);
}
