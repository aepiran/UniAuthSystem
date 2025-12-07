package ai.uniauth.service.dto;

import ai.uniauth.models.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private UserStatus status;
    private String phoneNumber;
    private String department;
    private String position;
    private String avatarUrl;
    private boolean mfaEnabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private Set<String> roles;
    private Set<String> permissions;
    private boolean isLocked;
    private LocalDateTime lockedUntil;
    private int failedLoginAttempts;
}