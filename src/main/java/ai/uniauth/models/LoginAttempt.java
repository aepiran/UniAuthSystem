package ai.uniauth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts",
        indexes = {
                @Index(name = "idx_login_attempts_user", columnList = "user_id"),
                @Index(name = "idx_login_attempts_ip", columnList = "ip_address"),
                @Index(name = "idx_login_attempts_time", columnList = "attempted_at"),
                @Index(name = "idx_login_attempts_success", columnList = "success")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class LoginAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "success")
    private Boolean success = false;

    @Column(name = "failure_reason", length = 100)
    private String failureReason;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String city;

    @Column(name = "mfa_used")
    private Boolean mfaUsed = false;

    @Column(name = "mfa_method", length = 50)
    private String mfaMethod;

    @PrePersist
    public void prePersist() {
        if (success == null) {
            success = false;
        }
        if (attemptedAt == null) {
            attemptedAt = LocalDateTime.now();
        }
        if (mfaUsed == null) {
            mfaUsed = false;
        }
    }
}