package ai.uniauth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_logs",
        indexes = {
                @Index(name = "idx_webhook_system", columnList = "system_id"),
                @Index(name = "idx_webhook_created", columnList = "created_at")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "system")
public class WebhookLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id", nullable = false, columnDefinition = "BINARY(16)")
    private UniSystem uniSystem;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(columnDefinition = "JSON")
    private String payload;

    @Column(length = 50)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "attempt_count")
    private Integer attemptCount = 1;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (attemptCount == null) {
            attemptCount = 1;
        }
    }
}