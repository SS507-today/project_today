package ssu.today.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import ssu.today.global.entity.BaseTimeEntity;

@Entity
@Table(name = "members")
@SQLRestriction("deleted_at is NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false)
    private Long authId;
    private String email;
    @Column
    private String name;
    @Column
    private String nickName;
    @Column(length = 2000)
    private String image; // 프로필 이미지 URL
    @Column
    private String platform;
    @Column
    private String refreshToken;     // Refresh Token
    @Column
    private String accessToken;      // Access Token

    public Member update(String name, String image) {
        this.name = name;
        this.image = image;
        return this;
    }
}
