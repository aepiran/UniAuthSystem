package ai.uniauth.service.impl;

import ai.uniauth.models.*;
import ai.uniauth.models.enums.ActionType;
import ai.uniauth.models.enums.UserStatus;
import ai.uniauth.rep.*;
import ai.uniauth.service.UserService;
import ai.uniauth.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRep userRep;
    private final RoleRep roleRep;
    private final UserRoleRep userRoleRep;
    private final UserSessionRep userSessionRep;
    private final LoginAttemptRep loginAttemptRep;
    private final PasswordResetRep passwordResetRep;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRep.findByUsernameOrEmail(username, username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .authorities(Collections.emptyList())
                        .accountExpired(false)
                        .accountLocked(user.getIsLocked())
                        .credentialsExpired(false)
                        .disabled(!user.getStatus().canLogin())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());

        // Validate unique constraints
        if (userRep.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRep.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Hash password
        String salt = generateSalt();
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash() + salt);
        user.setPasswordHash(hashedPassword);
        user.setPasswordSalt(salt);

        // Set default status
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setLastPasswordChange(LocalDateTime.now());

        User savedUser = userRep.save(user);
//        // Send welcome notification
//        if (savedUser.getStatus() == UserStatus.ACTIVE) {
//            notificationService.sendWelcomeNotification(savedUser.getId());
//        }

        log.info("User created successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, User updates) {
        log.info("Updating user: {}", userId);

        User user = getUserById(userId);

        // Update fields
        if (updates.getFullName() != null) {
            user.setFullName(updates.getFullName());
        }
        if (updates.getPhoneNumber() != null) {
            user.setPhoneNumber(updates.getPhoneNumber());
        }
        if (updates.getDepartment() != null) {
            user.setDepartment(updates.getDepartment());
        }
        if (updates.getPosition() != null) {
            user.setPosition(updates.getPosition());
        }
        if (updates.getAvatarUrl() != null) {
            user.setAvatarUrl(updates.getAvatarUrl());
        }
        if (updates.getTimezone() != null) {
            user.setTimezone(updates.getTimezone());
        }
        if (updates.getLocale() != null) {
            user.setLocale(updates.getLocale());
        }
        if (updates.getMetadata() != null) {
            user.setMetadata(updates.getMetadata());
        }

        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUser().getUsername());

        User updatedUser = userRep.save(user);

        // Audit log
//        auditService.logUserUpdate(getCurrentUserId(), user, updates);

        log.info("User updated successfully: {}", userId);
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRep.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRep.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRep.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Deleting user: {}", userId);

        User user = getUserById(userId);

        // Check if user can be deleted
        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("User already deleted: " + userId);
        }

        // Soft delete
        user.setStatus(UserStatus.DELETED);
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUser().getUsername());

        userRep.save(user);

        // Audit log
//        auditService.logUserDeletion(getCurrentUserId(), user);

        log.info("User deleted successfully: {}", userId);
    }

    @Override
    @Transactional
    public User changeUserStatus(UUID userId, ActionType action, String reason) {
        User user = getUserById(userId);
        UserStatus currentStatus = user.getStatus();

        // Check if action is applicable
        if (!currentStatus.canApplyAction(action)) {
            throw new IllegalArgumentException(
                    String.format("Action %s cannot be applied to user with status %s",
                            action, currentStatus));
        }

        // Get next status
        UserStatus nextStatus = currentStatus.applyAction(action);

        // Update user
        user.setStatus(nextStatus);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUser().getUsername());

        // Handle special cases
        if (action == ActionType.LOCK_USER) {
            user.setIsLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusHours(24)); // 24-hour lock
        } else if (action == ActionType.UNLOCK_USER) {
            user.setIsLocked(false);
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
        } else if (action == ActionType.FORCE_PASSWORD_RESET) {
            user.setMustChangePassword(true);
        }

        User updatedUser = userRep.save(user);

//        // Audit log
//        auditService.logUserStatusChange(getCurrentUserId(), user, action,
//                currentStatus, nextStatus, reason);
//
//        // Send notification
//        notificationService.sendStatusChangeNotification(userId, currentStatus, nextStatus, reason);

        log.info("User {} status changed from {} to {} by {}",
                userId, currentStatus, nextStatus, getCurrentUser().getUsername());

        return updatedUser;
    }

    @Override
    @Transactional
    public User lockUser(UUID userId, String reason) {
        return changeUserStatus(userId, ActionType.LOCK_USER, reason);
    }

    @Override
    @Transactional
    public User unlockUser(UUID userId, String reason) {
        return changeUserStatus(userId, ActionType.UNLOCK_USER, reason);
    }

    @Override
    public User suspendUser(UUID userId, String reason) {
        return null;
    }

    @Override
    public User unsuspendUser(UUID userId, String reason) {
        return null;
    }

    @Override
    public User activateUser(UUID userId, String reason) {
        return null;
    }

    @Override
    public User deactivateUser(UUID userId, String reason) {
        return null;
    }

    @Override
    public User archiveUser(UUID userId, String reason) {
        return null;
    }

    @Override
    public User restoreUser(UUID userId, String reason) {
        return null;
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify old password
        String hashedOldPassword = passwordEncoder.encode(oldPassword + user.getPasswordSalt());
        if (!hashedOldPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update password
        String newSalt = generateSalt();
        String hashedNewPassword = passwordEncoder.encode(newPassword + newSalt);

        user.setPasswordHash(hashedNewPassword);
        user.setPasswordSalt(newSalt);
        user.setLastPasswordChange(LocalDateTime.now());
        user.setMustChangePassword(false);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getUsername());

        userRep.save(user);

//        // Audit log
//        auditService.logPasswordChange(getCurrentUserId(), user);
//
//        // Send notification
//        notificationService.sendPasswordChangedNotification(userId);
    }

    @Override
    public void resetPassword(UUID userId, String newPassword) {
        User user = getUserById(userId);
        String newSalt = generateSalt();
        String hashedNewPassword = passwordEncoder.encode(newPassword + newSalt);

        user.setPasswordHash(hashedNewPassword);
        user.setPasswordSalt(newSalt);
        user.setLastPasswordChange(LocalDateTime.now());
        userRep.save(user);
    }

    @Override
    public void forcePasswordReset(UUID userId) {

    }

    @Override
    public boolean validatePassword(UUID userId, String password) {
        User user = getUserById(userId);

        String hashedOldPassword = passwordEncoder.encode(password + user.getPasswordSalt());
        if (hashedOldPassword.equals(user.getPasswordHash())) {
            return true;
        }
        return false;
    }

    @Override
    public String generatePasswordResetToken(UUID userId) {
        return "";
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        return false;
    }

    @Override
    public void processPasswordReset(String token, String newPassword) {

    }

    @Override
    public void enableMfa(UUID userId, String secret) {

    }

    @Override
    public void disableMfa(UUID userId) {

    }

    @Override
    public boolean verifyMfa(UUID userId, String code) {
        return false;
    }

    @Override
    public String generateMfaSecret(UUID userId) {
        return "";
    }

    @Override
    public boolean isMfaEnabled(UUID userId) {
        return false;
    }

    @Override
    public User updateProfile(UUID userId, Map<String, Object> updates) {
        return null;
    }

    @Override
    public User updateAvatar(UUID userId, String avatarUrl) {
        return null;
    }

    @Override
    public User updateSettings(UUID userId, Map<String, Object> settings) {
        return null;
    }

    @Override
    @Transactional
    public void assignRole(UUID userId, UUID roleId, String assignedBy) {
        User user = getUserById(userId);

        // Check if role already assigned
        if (userRoleRep.existsByUserIdAndRoleId(userId, roleId)) {
            throw new IllegalArgumentException("Role already assigned to user");
        }

        Role role = roleRep.findById(roleId).orElseThrow(IllegalArgumentException::new);

        // Assign role
        userRoleRep.save(UserRole.builder()
                .user(user)
                .role(role)
                .assignedAt(LocalDateTime.now())
                .assignedBy(assignedBy)
                .build());

        // Audit log
//        auditService.logRoleAssignment(getCurrentUserId(), user, roleId);

        log.info("Role {} assigned to user {} by {}", roleId, userId, assignedBy);
    }

    @Override
    public void assignRoles(UUID userId, Set<UUID> roleIds, String assignedBy) {

    }

    @Override
    public void revokeRole(UUID userId, UUID roleId) {

    }

    @Override
    public void revokeAllRoles(UUID userId) {

    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserRoles(UUID userId) {
        return userRoleRep.findByUserId(userId).stream()
                .map(userRole -> userRole.getRole().getCode())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(UUID userId) {
        User user = userRep.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(RolePermission::getPermission)
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasRole(UUID userId, String roleCode) {
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, String permissionCode) {
        Set<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode);
    }

    @Override
    public boolean hasAnyPermission(UUID userId, Set<String> permissionCodes) {
        return false;
    }

    @Override
    public boolean hasAllPermissions(UUID userId, Set<String> permissionCodes) {
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRep.search(keyword, pageable);
    }

    @Override
    public Page<User> filterUsers(Map<String, Object> filters, Pageable pageable) {
        return null;
    }

    @Override
    public List<User> getUsersByStatus(UserStatus status) {
        return userRep.findByStatus(status);
    }

    @Override
    public List<User> getUsersByDepartment(String department) {
        return userRep.findByDepartment(department);
    }

    @Override
    public List<User> getUsersByRole(String roleCode) {

        return userRep.findByRoleCode(roleCode);
    }

    @Override
    public List<User> getUsersBySystem(String systemCode) {
        return userRep.findBySystemCode(systemCode);
    }

    @Override
    @Transactional
    public void bulkChangeStatus(Set<UUID> userIds, ActionType action, String reason) {
        for (UUID userId : userIds) {
            try {
                changeUserStatus(userId, action, reason);
            } catch (Exception e) {
                log.error("Failed to change status for user {}: {}", userId, e.getMessage());
            }
        }

        log.info("Bulk status change completed for {} users", userIds.size());
    }

    @Override
    public void bulkAssignRoles(Set<UUID> userIds, Set<UUID> roleIds, String assignedBy) {

    }

    @Override
    public void bulkRevokeRoles(Set<UUID> userIds, Set<UUID> roleIds) {

    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, UserStatus> bulkGetStatus(Set<UUID> userIds) {
        List<User> users = userRep.findAllById(userIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, User::getStatus));
    }

    @Override
    public User authenticate(String usernameOrEmail, String password) {
        return null;
    }

    @Override
    public void recordLoginAttempt(UUID userId, boolean success, String ipAddress, String userAgent) {

    }

    @Override
    public void updateLastLogin(UUID userId) {

    }

    @Override
    public int getFailedLoginAttempts(UUID userId) {
        return 0;
    }

    @Override
    public void resetFailedLoginAttempts(UUID userId) {

    }

    @Override
    public long countUsers() {
        return 0;
    }

    @Override
    public long countUsersByStatus(UserStatus status) {
        return 0;
    }

    @Override
    public Map<String, Long> getUserStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return Map.of();
    }

    @Override
    public List<Map<String, Object>> getUserActivityReport(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public List<User> getInactiveUsers(LocalDateTime cutoffDate) {
        return List.of();
    }

    @Override
    public List<User> importUsers(List<User> users, String importedBy) {
        return List.of();
    }

    @Override
    public byte[] exportUsers(List<UUID> userIds, String format) {
        return new byte[0];
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return false;
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return false;
    }

    @Override
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return false;
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // Helper methods
    private String generateSalt() {
        return UUID.randomUUID().toString().substring(0, 16);
    }

    @Override
    public boolean isCurrentUser(UUID userId) {
        return false;
    }

    @Override
    public boolean isAdminUser(UUID userId) {
        return false;
    }

    @Override
    public void logUserAction(UUID userId, ActionType action, String entityType, UUID entityId, Map<String, Object> oldValues, Map<String, Object> newValues) {

    }
}