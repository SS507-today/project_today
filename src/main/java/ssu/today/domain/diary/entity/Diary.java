package ssu.today.domain.diary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.global.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diaries")
@SQLRestriction("deleted_at is NULL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;
    @Column(nullable = false, length = 1000)
    private String finalDiaryImage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_bundle_id")
    private DiaryBundle diaryBundle;

    @OneToMany(mappedBy = "diary")
    @Builder.Default
    private List<DiaryTag> diaryTagList = new ArrayList<>();


    public void delete() {
        for (DiaryTag diaryTag : diaryTagList) {
            diaryTag.delete();
        }
        super.delete();
    }
}
