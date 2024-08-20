package ssu.today.domain.shareGroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import ssu.today.global.entity.BaseTimeEntity;

@Entity
@Table(name = "share_groups")
@SQLRestriction("deleted_at is NULL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShareGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_group_id")
    private Long id;
    @Column(name = "name", length = 15, nullable = false)
    private String name;
    @Column(name = "description", length = 50)
    private String description;
    @Column(name = "max_members", nullable = false)
    private int maxMembers;
    @Column(name = "invite_code", nullable = false)
    private String inviteCode;
    @Column(name = "cover_image", nullable = false)
    private int coverImage;
    @Column(name = "rule_first", length = 30)
    private int ruleFirst;
    @Column(name = "rule_second", length = 30)
    private int ruleSecond;
    @Column(name = "rule_third", length = 30)
    private int ruleThird;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
