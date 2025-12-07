package ai.uniauth.models;

import ai.uniauth.models.enums.AuthType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unisystems",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_systems_code", columnList = "code"),
                @Index(name = "idx_systems_is_active", columnList = "is_active")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "permissions", "apiKeys", "webhookLogs"})
@ToString(exclude = {"roles", "permissions", "apiKeys", "webhookLogs"})
public class UniSystem extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 500)
    @Column(name = "base_url", length = 500)
    private String baseUrl;

    @Size(max = 500)
    @Column(name = "health_check_url", length = 500)
    private String healthCheckUrl;

    @Size(max = 500)
    @Column(name = "api_endpoint", length = 500)
    private String apiEndpoint;

    @Size(max = 500)
    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Size(max = 255)
    @Column(name = "webhook_secret", length = 255)
    private String webhookSecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", length = 50)
    private AuthType authType = AuthType.JWT;

    @Size(max = 255)
    @Column(name = "client_id", length = 255)
    private String clientId;

    @Size(max = 255)
    @Column(name = "client_secret", length = 255)
    private String clientSecret;

    @Size(max = 255)
    @Column(name = "jwt_issuer", length = 255)
    private String jwtIssuer;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;

    @Column(columnDefinition = "JSON")
    private String config;

    @Column(name = "rate_limit")
    private Integer rateLimit = 1000;

    @Column(name = "timeout_ms")
    private Integer timeoutMs = 5000;

    // Relationships
    @OneToMany(mappedBy = "uniSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "uniSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "uniSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ApiKey> apiKeys = new HashSet<>();

    @OneToMany(mappedBy = "uniSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<WebhookLog> webhookLogs = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (authType == null) {
            authType = AuthType.JWT;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isInternal == null) {
            isInternal = false;
        }
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
        if (rateLimit == null) {
            rateLimit = 1000;
        }
        if (timeoutMs == null) {
            timeoutMs = 5000;
        }
    }
}