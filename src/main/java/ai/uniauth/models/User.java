package ai.uniauth.models;

import ai.uniauth.models.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_department", columnList = "department")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "sessions", "loginAttempts"})
@ToString(exclude = {"password", "roles", "sessions", "loginAttempts"})
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @NaturalId
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 50)
    @Column(name = "password_salt", length = 50)
    private String passwordSalt;

    @Size(max = 100)
    @Column(name = "full_name", length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(max = 100)
    @Column(length = 100)
    private String department;

    @Size(max = 100)
    @Column(length = 100)
    private String position;

    @Size(max = 500)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "timezone", length = 50)
    private String timezone = "Asia/Ho_Chi_Minh";

    @Column(length = 10)
    private String locale = "vi-VN";

    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled = false;

    @Size(max = 100)
    @Column(name = "mfa_secret", length = 100)
    private String mfaSecret;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword = false;

    @Column(columnDefinition = "JSON")
    private String metadata;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            indexes = {
                    @Index(name = "idx_user_roles_user", columnList = "user_id"),
                    @Index(name = "idx_user_roles_role", columnList = "role_id")
            })
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserSession> sessions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<LoginAttempt> loginAttempts = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
        if (isLocked == null) {
            isLocked = false;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (mfaEnabled == null) {
            mfaEnabled = false;
        }
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
        if (mustChangePassword == null) {
            mustChangePassword = false;
        }
    }
}