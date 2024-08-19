package ssu.today.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ssu.today.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
@SQLRestriction("deleted_at is NULL")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    @Column(nullable = false)
    protected String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "is_checked")
    private boolean isChecked;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id") //알림 생성자
    private Member actor;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    public abstract void postMessage();
    public abstract Notification makeOtherNotification(Member member);
    public abstract String makeNotificationInfoURL();
    public void notificationAcknowledge(boolean isChecked){
        this.isChecked = isChecked;
    }
}
