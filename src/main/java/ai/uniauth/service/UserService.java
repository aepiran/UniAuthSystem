package ai.uniauth.service;

import ai.uniauth.models.User;
import ai.uniauth.models.enums.ActionType;
import ai.uniauth.models.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    // Basic CRUD Operations
    User createUser(User user);
    User updateUser(UUID userId, User user);
    User getUserById(UUID userId);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    void deleteUser(UUID userId);

    // User Status Management
    User changeUserStatus(UUID userId, ActionType action, String reason);
    User lockUser(UUID userId, String reason);
    User unlockUser(UUID userId, String reason);
    User suspendUser(UUID userId, String reason);
    User unsuspendUser(UUID userId, String reason);
    User activateUser(UUID userId, String reason);
    User deactivateUser(UUID userId, String reason);
    User archiveUser(UUID userId, String reason);
    User restoreUser(UUID userId, String reason);

    // Password Management
    void changePassword(UUID userId, String oldPassword, String newPassword);
    void resetPassword(UUID userId, String newPassword);
    void forcePasswordReset(UUID userId);
    boolean validatePassword(UUID userId, String password);
    String generatePasswordResetToken(UUID userId);
    boolean validatePasswordResetToken(String token);
    void processPasswordReset(String token, String newPassword);

    // MFA Management
    void enableMfa(UUID userId, String secret);
    void disableMfa(UUID userId);
    boolean verifyMfa(UUID userId, String code);
    String generateMfaSecret(UUID userId);
    boolean isMfaEnabled(UUID userId);

    // Profile Management
    User updateProfile(UUID userId, Map<String, Object> updates);
    User updateAvatar(UUID userId, String avatarUrl);
    User updateSettings(UUID userId, Map<String, Object> settings);

    // Role Management
    void assignRole(UUID userId, UUID roleId, String assignedBy);
    void assignRoles(UUID userId, Set<UUID> roleIds, String assignedBy);
    void revokeRole(UUID userId, UUID roleId);
    void revokeAllRoles(UUID userId);
    Set<String> getUserRoles(UUID userId);
    Set<String> getUserPermissions(UUID userId);
    boolean hasRole(UUID userId, String roleCode);
    boolean hasPermission(UUID userId, String permissionCode);
    boolean hasAnyPermission(UUID userId, Set<String> permissionCodes);
    boolean hasAllPermissions(UUID userId, Set<String> permissionCodes);

    // Search and Filter
    Page<User> searchUsers(String keyword, Pageable pageable);
    Page<User> filterUsers(Map<String, Object> filters, Pageable pageable);
    List<User> getUsersByStatus(UserStatus status);
    List<User> getUsersByDepartment(String department);
    List<User> getUsersByRole(String roleCode);
    List<User> getUsersBySystem(String systemCode);

    // Bulk Operations
    void bulkChangeStatus(Set<UUID> userIds, ActionType action, String reason);
    void bulkAssignRoles(Set<UUID> userIds, Set<UUID> roleIds, String assignedBy);
    void bulkRevokeRoles(Set<UUID> userIds, Set<UUID> roleIds);
    Map<UUID, UserStatus> bulkGetStatus(Set<UUID> userIds);

    // Authentication & Login
    User authenticate(String usernameOrEmail, String password);
    void recordLoginAttempt(UUID userId, boolean success, String ipAddress, String userAgent);
    void updateLastLogin(UUID userId);
    int getFailedLoginAttempts(UUID userId);
    void resetFailedLoginAttempts(UUID userId);

    // Statistics and Reporting
    long countUsers();
    long countUsersByStatus(UserStatus status);
    Map<String, Long> getUserStatistics(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getUserActivityReport(LocalDateTime startDate, LocalDateTime endDate);
    List<User> getInactiveUsers(LocalDateTime cutoffDate);

    // Import/Export
    List<User> importUsers(List<User> users, String importedBy);
    byte[] exportUsers(List<UUID> userIds, String format);

    // Validation
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);
    boolean isPhoneNumberAvailable(String phoneNumber);

    // Helper Methods
    User getCurrentUser();
    UUID getCurrentUserId();
    boolean isCurrentUser(UUID userId);
    boolean isAdminUser(UUID userId);

    // Audit Logging
    void logUserAction(UUID userId, ActionType action, String entityType, UUID entityId,
                       Map<String, Object> oldValues, Map<String, Object> newValues);
}