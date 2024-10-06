package ssu.today.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.diary.entity.Diary;
import ssu.today.domain.diary.entity.DiaryBundle;
import ssu.today.domain.shareGroup.entity.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 특정 번들에 속한 다이어리들을 최신순으로 조회
    List<Diary> findAllByDiaryBundle_IdAndDiaryBundle_ShareGroup_IdOrderByCreatedAtDesc(Long diaryBundleId, Long shareGroupId);
    // 오늘 이미 다이어리를 업로드했는지 확인
    boolean existsByProfileAndCreatedAt(Profile profile, LocalDateTime createdAt);
    // 특정 DiaryBundle 내에서 해당 프로필로 작성된 다이어리의 수를 확인
    int countByDiaryBundleAndProfile(DiaryBundle diaryBundle, Profile profile);
}
