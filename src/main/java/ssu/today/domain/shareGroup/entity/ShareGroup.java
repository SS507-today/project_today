package ssu.today.domain.shareGroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import ssu.today.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "share_groups")
@SQLRestriction("deleted_at is NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShareGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_group_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "max_members", nullable = false)
    private int memberCount;
    @Column(name = "invite_code", nullable = false)
    private String inviteCode;
    @Column(name = "cover_image", nullable = false)
    private int coverImage;
    @Column(name = "rule_first")
    private String ruleFirst;
    @Column(name = "rule_second")
    private String ruleSecond;
    @Column(name = "rule_third")
    private String ruleThird;
    @Column(name = "open_at")
    private LocalDateTime openAt;  // 새로운 openAt 필드 추가

    private String ownerName; //공유그룹 오너의 이름

    @OneToMany(mappedBy = "shareGroup")
    @Builder.Default
    private List<Profile> profileList = new ArrayList<>();

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

}
