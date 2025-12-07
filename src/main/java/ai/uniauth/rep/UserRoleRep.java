package ai.uniauth.rep;

import ai.uniauth.models.Role;
import ai.uniauth.models.User;
import ai.uniauth.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface UserRoleRep extends JpaRepository<UserRole, UUID> {

    // Basic Finders
    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);
    List<UserRole> findByUserId(UUID userId);
    List<UserRole> findByRoleId(UUID roleId);
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);

    // Count Queries
    long countByUserId(UUID userId);
    long countByRoleId(UUID roleId);

    // Existence Checks
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    // Expiration-based Finders
    @Query("SELECT ur FROM UserRole ur WHERE ur.expiresAt IS NOT NULL AND ur.expiresAt < CURRENT_TIMESTAMP")
    List<UserRole> findExpiredUserRoles();

    @Query("SELECT ur FROM UserRole ur WHERE ur.expiresAt IS NOT NULL AND ur.expiresAt BETWEEN :start AND :end")
    List<UserRole> findExpiringUserRoles(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    // Temporary Roles
    List<UserRole> findByIsTemporaryTrue();
    List<UserRole> findByIsTemporaryFalse();

    // Bulk Operations
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id IN :roleIds")
    List<UserRole> findByUserIdAndRoleIds(@Param("userId") UUID userId,
                                          @Param("roleIds") Set<UUID> roleIds);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId AND ur.user.id IN :userIds")
    List<UserRole> findByRoleIdAndUserIds(@Param("roleId") UUID roleId,
                                          @Param("userIds") Set<UUID> userIds);

    // Update Queries
    @Modifying
    @Query("UPDATE UserRole ur SET ur.expiresAt = :expiresAt, ur.isTemporary = :isTemporary WHERE ur.id = :id")
    int updateExpiration(@Param("id") UUID id,
                         @Param("expiresAt") LocalDateTime expiresAt,
                         @Param("isTemporary") Boolean isTemporary);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId")
    int deleteByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id IN :roleIds")
    int deleteByUserIdAndRoleIds(@Param("userId") UUID userId, @Param("roleIds") Set<UUID> roleIds);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.role.id = :roleId")
    int deleteByRoleId(@Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    // Statistics
    @Query("SELECT ur.role.id, COUNT(ur) FROM UserRole ur WHERE ur.role.id IN :roleIds GROUP BY ur.role.id")
    List<Object[]> countUsersByRoleIds(@Param("roleIds") Set<UUID> roleIds);

    // Find users with specific roles
    @Query("SELECT ur.user.id FROM UserRole ur WHERE ur.role.id = :roleId")
    Set<Long> findUserIdsByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT ur.role.id FROM UserRole ur WHERE ur.user.id = :userId")
    Set<Long> findRoleIdsByUserId(@Param("userId") UUID userId);

    // Check if user has any of given roles
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id IN :roleIds")
    boolean existsByUserIdAndRoleIds(@Param("userId") UUID userId, @Param("roleIds") Set<UUID> roleIds);

    // Check if user has role with code
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur " +
            "JOIN ur.role r " +
            "WHERE ur.user.id = :userId AND r.code = :roleCode")
    boolean existsByUserIdAndRoleCode(@Param("userId") UUID userId, @Param("roleCode") String roleCode);
}