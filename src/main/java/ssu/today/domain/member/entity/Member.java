package ssu.today.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ssu.today.domain.shareGroup.entity.Profile;
import ssu.today.global.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

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
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
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

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Profile> profileList = new ArrayList<>();

    public void delete() {
        for (Profile profile : profileList) {
            profile.delete();
        }
        super.delete();
    }
}
