package ai.uniauth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_resets",
        indexes = {
                @Index(name = "idx_password_resets_token", columnList = "token"),
                @Index(name = "idx_password_resets_user", columnList = "user_id"),
                @Index(name = "idx_password_resets_expires", columnList = "expires_at")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class PasswordReset extends BaseNoIdEntity {

    @Id
    @Column(name = "token", length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_used")
    private Boolean isUsed = false;

    @Column(name = "used_ip")
    private String usedIp;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isUsed == null) {
            isUsed = false;
        }
    }
}