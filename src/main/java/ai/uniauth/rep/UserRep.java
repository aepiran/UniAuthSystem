package ai.uniauth.rep;

import ai.uniauth.models.User;
import ai.uniauth.models.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRep extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    // Basic Finders
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // Status-based Finders
    List<User> findByStatus(UserStatus status);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    long countByStatus(UserStatus status);

    List<User> findByIsLockedTrue();
    List<User> findByIsLockedFalse();

    // Date-based Finders
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<User> findByLastLoginAtBefore(LocalDateTime date);
    List<User> findByLastLoginAtIsNull();
    List<User> findByLastPasswordChangeBefore(LocalDateTime date);

    // Search Queries
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.department = :department")
    List<User> findByDepartment(@Param("department") String department);

    @Query("SELECT u FROM User u WHERE u.department IN :departments")
    List<User> findByDepartments(@Param("departments") Set<String> departments);

    // Role-based Finders
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.code = :roleCode")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.code IN :roleCodes")
    List<User> findByRoleCodes(@Param("roleCodes") Set<String> roleCodes);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(@Param("roleId") UUID roleId);

    // System-based Finders
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "JOIN r.uniSystem s " +
            "WHERE s.code = :systemCode")
    List<User> findBySystemCode(@Param("systemCode") String systemCode);

    // Statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND u.createdAt >= :startDate")
    long countByStatusSince(@Param("status") UserStatus status, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u " +
            "JOIN u.sessions s " +
            "WHERE s.loginAt >= :startDate AND s.loginAt <= :endDate")
    long countActiveUsersBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // Update Queries
    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateStatus(@Param("userId") UUID userId, @Param("status") UserStatus status);

    @Modifying
    @Query("UPDATE User u SET u.isLocked = :locked, u.lockedUntil = :lockedUntil, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateLockStatus(@Param("userId") UUID userId,
                         @Param("locked") Boolean locked,
                         @Param("lockedUntil") LocalDateTime lockedUntil);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt, " +
            "u.failedLoginAttempts = 0, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateLastLogin(@Param("userId") UUID userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int incrementFailedLoginAttempts(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int resetFailedLoginAttempts(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.mfaEnabled = :enabled, u.mfaSecret = :secret, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateMfaStatus(@Param("userId") UUID userId,
                        @Param("enabled") Boolean enabled,
                        @Param("secret") String secret);

    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :passwordHash, " +
            "u.lastPasswordChange = CURRENT_TIMESTAMP, " +
            "u.mustChangePassword = false, " +
            "u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updatePassword(@Param("userId") UUID userId, @Param("passwordHash") String passwordHash);

    // Bulk Operations
    @Modifying
    @Query("UPDATE User u SET u.status = :newStatus WHERE u.status = :oldStatus AND u.lastLoginAt < :cutoffDate")
    int deactivateInactiveUsers(@Param("oldStatus") UserStatus oldStatus,
                                @Param("newStatus") UserStatus newStatus,
                                @Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :cutoffDate")
    int deleteInactiveUsers(@Param("status") UserStatus status,
                            @Param("cutoffDate") LocalDateTime cutoffDate);

    // Join Fetch Queries
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :userId")
    Optional<User> findByIdWithRolesAndPermissions(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.sessions WHERE u.id = :userId")
    Optional<User> findByIdWithSessions(@Param("userId") UUID userId);
}