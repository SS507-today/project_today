package ssu.today.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssu.today.domain.diary.entity.DiaryTag;

@Repository
public interface TagRepository extends JpaRepository<DiaryTag, Long> {
}
