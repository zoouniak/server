package com.example.cns.notification.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.notification.type.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private Member to;

    @Column
    private Long subjectId;

    // todo converter 사용 고민
    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType notificationType;

    @Column(nullable = false)
    private String message;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("false")
    private boolean isChecked;

    @Builder
    public Notification(Member to, Long subjectId, NotificationType notificationType, String message, LocalDateTime createdAt) {
        this.to = to;
        this.subjectId = subjectId;
        this.notificationType = notificationType;
        this.message = message;
        this.createdAt = createdAt;
        this.isChecked = false;
    }
}
