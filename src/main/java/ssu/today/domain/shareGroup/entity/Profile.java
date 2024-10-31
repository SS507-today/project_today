package ssu.today.domain.shareGroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import ssu.today.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@SQLRestriction("deleted_at is NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String profileNickName; // 그룹 내 닉네임
    @Column(name = "image", length = 2000)
    private String image; // 그룹 내 이미지
    @Column(name = "description", length = 30)
    private String description; //그룹 내 소개
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "is_my_turn")
    private Boolean isMyTurn; // 내 차례인지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_group_id")
    private ShareGroup shareGroup;
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void setInfo(Member member) {
        this.member = member;
        this.image = member.getImage();
        this.joinedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}

