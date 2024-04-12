package com.example.cns.notification.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.notification.type.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "from_id")
    private Member from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private Member to;

    @Column
    private Long subjectId;

    // todo converter 사용 고민
    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType notificationType;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("false")
    private boolean isChecked;
}
