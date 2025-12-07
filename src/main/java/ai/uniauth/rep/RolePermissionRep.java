package ai.uniauth.rep;

import ai.uniauth.models.Permission;
import ai.uniauth.models.Role;
import ai.uniauth.models.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RolePermissionRep extends JpaRepository<RolePermission, UUID> {

    // Basic Finders
    Optional<RolePermission> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
    List<RolePermission> findByRoleId(UUID roleId);
    List<RolePermission> findByPermissionId(UUID permissionId);
    List<RolePermission> findByRole(Role role);
    List<RolePermission> findByPermission(Permission permission);

    // Count Queries
    long countByRoleId(UUID roleId);
    long countByPermissionId(UUID permissionId);

    // Existence Checks
    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    // Access Level Finders
    List<RolePermission> findByAccessLevel(String accessLevel);
    List<RolePermission> findByRoleIdAndAccessLevel(UUID roleId, String accessLevel);

    // Bulk Operations
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id IN :permissionIds")
    List<RolePermission> findByRoleIdAndPermissionIds(@Param("roleId") UUID roleId,
                                                      @Param("permissionIds") Set<UUID> permissionIds);

    @Query("SELECT rp FROM RolePermission rp WHERE rp.permission.id = :permissionId AND rp.role.id IN :roleIds")
    List<RolePermission> findByPermissionIdAndRoleIds(@Param("permissionId") UUID permissionId,
                                                      @Param("roleIds") Set<UUID> roleIds);

    // Update Queries
    @Modifying
    @Query("UPDATE RolePermission rp SET rp.accessLevel = :accessLevel WHERE rp.id = :id")
    int updateAccessLevel(@Param("id") UUID id, @Param("accessLevel") String accessLevel);

    @Modifying
    @Query("UPDATE RolePermission rp SET rp.conditions = :conditions WHERE rp.id = :id")
    int updateConditions(@Param("id") UUID id, @Param("conditions") String conditions);

    @Modifying
    @Query("UPDATE RolePermission rp SET rp.scopeFilter = :scopeFilter WHERE rp.id = :id")
    int updateScopeFilter(@Param("id") UUID id, @Param("scopeFilter") String scopeFilter);

    // Delete Queries
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId")
    int deleteByRoleIdAndPermissionId(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    int deleteByRoleId(@Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.permission.id = :permissionId")
    int deleteByPermissionId(@Param("permissionId") UUID permissionId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id IN :permissionIds")
    int deleteByRoleIdAndPermissionIds(@Param("roleId") UUID roleId,
                                       @Param("permissionIds") Set<UUID> permissionIds);

    // Statistics
    @Query("SELECT rp.role.id, COUNT(rp) FROM RolePermission rp WHERE rp.role.id IN :roleIds GROUP BY rp.role.id")
    List<Object[]> countPermissionsByRoleIds(@Param("roleIds") Set<UUID> roleIds);

    @Query("SELECT rp.permission.id, COUNT(rp) FROM RolePermission rp WHERE rp.permission.id IN :permissionIds GROUP BY rp.permission.id")
    List<Object[]> countRolesByPermissionIds(@Param("permissionIds") Set<UUID> permissionIds);

    // Find permissions for roles
    @Query("SELECT DISTINCT rp.permission.id FROM RolePermission rp WHERE rp.role.id = :roleId")
    Set<UUID> findPermissionIdsByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT DISTINCT rp.permission.code FROM RolePermission rp " +
            "JOIN rp.permission p " +
            "WHERE rp.role.id = :roleId AND rp.accessLevel = 'ALLOW'")
    Set<String> findPermissionCodesByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT DISTINCT rp.role.id FROM RolePermission rp WHERE rp.permission.id = :permissionId")
    Set<UUID> findRoleIdsByPermissionId(@Param("permissionId") UUID permissionId);

    // Check if role has permission
    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp " +
            "WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId AND rp.accessLevel = 'ALLOW'")
    boolean hasPermission(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);

    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp " +
            "JOIN rp.permission p " +
            "WHERE rp.role.id = :roleId AND p.code = :permissionCode AND rp.accessLevel = 'ALLOW'")
    boolean hasPermissionByCode(@Param("roleId") UUID roleId, @Param("permissionCode") String permissionCode);
}