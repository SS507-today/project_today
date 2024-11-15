package ssu.today.domain.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.shareGroup.entity.ShareGroup;

import java.util.Optional;

@Repository
public interface DiaryBundleRepository  extends JpaRepository<DiaryBundle, Long> {
    // 공유 그룹에 속한 가장 최신 DiaryBundle을 가져오는 메서드
    Optional<DiaryBundle> findFirstByShareGroupOrderByStartedAtDesc(ShareGroup shareGroup);
    // 특정 공유 그룹의 번들을 페이징 처리하여 가져오는 메서드
    Page<DiaryBundle> findByShareGroupId(Long shareGroupId, Pageable pageable);
}
