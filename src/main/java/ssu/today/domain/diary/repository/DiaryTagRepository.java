package ssu.today.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.diary.entity.DiaryTag;

import java.util.List;

@Repository
public interface DiaryTagRepository extends JpaRepository<DiaryTag, Long> {
    // 다이어리 ID를 통해 태그된 DiaryTag 목록을 조회하는 메서드
    List<DiaryTag> findAllByDiaryId(Long diaryId);
}
