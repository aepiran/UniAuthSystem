package ai.uniauth.models;


import ai.uniauth.models.enums.DeviceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions",
        indexes = {
                @Index(name = "idx_sessions_user", columnList = "user_id"),
                @Index(name = "idx_sessions_refresh_token", columnList = "refresh_token"),
                @Index(name = "idx_sessions_is_active", columnList = "is_active"),
                @Index(name = "idx_sessions_expires", columnList = "access_token_expires")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(exclude = {"accessToken", "refreshToken", "tokenHash"})
public class UserSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 50)
    private DeviceType deviceType;

    @Column(name = "device_name", length = 200)
    private String deviceName;

    @Column(length = 100)
    private String os;

    @Column(length = 100)
    private String browser;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "access_token", length = 255)
    private String accessToken;

    @Column(name = "refresh_token", length = 255, unique = true)
    private String refreshToken;

    @Column(name = "token_hash", length = 255)
    private String tokenHash;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "access_token_expires")
    private LocalDateTime accessTokenExpires;

    @Column(name = "refresh_token_expires")
    private LocalDateTime refreshTokenExpires;

    @Column(name = "logout_at")
    private LocalDateTime logoutAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "logout_reason", length = 100)
    private String logoutReason;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(length = 100)
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @PrePersist
    public void prePersist() {
        if (loginAt == null) {
            loginAt = LocalDateTime.now();
        }
        if (lastActivityAt == null) {
            lastActivityAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}