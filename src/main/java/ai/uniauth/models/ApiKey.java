package ai.uniauth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys",
        indexes = {
                @Index(name = "idx_api_keys_system", columnList = "system_id"),
                @Index(name = "idx_api_keys_active", columnList = "is_active"),
                @Index(name = "idx_api_keys_expires", columnList = "expires_at")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "uiSystem")
@ToString(exclude = {"apiKeyHash", "clientSecret"})
public class ApiKey extends BaseEntity {
    @Column(name = "api_key_hash", nullable = false, length = 255, unique = true)
    private String apiKeyHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id", nullable = false, columnDefinition = "BINARY(16)")
    private UniSystem uniSystem;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    private String permissions;

    @Column(name = "rate_limit")
    private Integer rateLimit = 100;

    @Column(name = "allowed_ips", columnDefinition = "inet[]")
    private String[] allowedIps;

    @Column(name = "allowed_origins", columnDefinition = "varchar(500)[]")
    private String[] allowedOrigins;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_reason", columnDefinition = "TEXT")
    private String revokedReason;

    @Column(name = "created_by")
    private String createdBy;

    @Column(columnDefinition = "JSON")
    private String metadata;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (rateLimit == null) {
            rateLimit = 100;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}