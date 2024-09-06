package ssu.today.domain.shareGroup.service;

import ssu.today.domain.member.entity.Member;
import ssu.today.domain.shareGroup.dto.ShareGroupRequest;
import ssu.today.domain.shareGroup.entity.ShareGroup;

public interface ShareGroupService {
    ShareGroup createShareGroup(ShareGroupRequest.createShareGroupRequest request,
                                Member member);
}
