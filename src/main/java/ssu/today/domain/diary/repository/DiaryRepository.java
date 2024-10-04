package ssu.today.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.diary.entity.Diary;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 특정 번들에 속한 다이어리들을 최신순으로 조회
    List<Diary> findAllByDiaryBundle_IdAndDiaryBundle_ShareGroup_IdOrderByCreatedAtDesc(Long diaryBundleId, Long shareGroupId);
}
