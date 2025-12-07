package ai.uniauth.rep;

import ai.uniauth.models.Role;
import ai.uniauth.models.UniSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRep extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {

    // Basic Finders
    Optional<Role> findByName(String name);
    Optional<Role> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);

    // UniSystem-based Finders
    List<Role> findByUniSystem(UniSystem system);
    List<Role> findByUniSystemId(UUID systemId);
    List<Role> findByUniSystemCode(String systemCode);
    List<Role> findByUniSystemIsNull();

    // Status-based Finders
    List<Role> findByIsSystemRoleTrue();
    List<Role> findByIsSystemRoleFalse();
    List<Role> findByIsDefaultTrue();
    List<Role> findByIsDefaultFalse();

    // Hierarchy Finders
    List<Role> findByParentId(UUID parentId);
    List<Role> findByParentIsNull();

    // Search Queries
    @Query("SELECT r FROM Role r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Role> search(@Param("keyword") String keyword, Pageable pageable);

    // User Count
    @Query("SELECT r.id, COUNT(u) FROM Role r LEFT JOIN r.users u WHERE r.id IN :roleIds GROUP BY r.id")
    List<Object[]> countUsersByRoleIds(@Param("roleIds") Set<UUID> roleIds);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") UUID roleId);

    // Permission-based Finders
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions rp JOIN rp.permission p WHERE p.code = :permissionCode")
    List<Role> findByPermissionCode(@Param("permissionCode") String permissionCode);

    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions rp JOIN rp.permission p WHERE p.id = :permissionId")
    List<Role> findByPermissionId(@Param("permissionId") UUID permissionId);

    // Update Queries
    @Modifying
    @Query("UPDATE Role r SET r.isDefault = :isDefault WHERE r.uniSystem = :system")
    int updateDefaultRolesByUniSystem(@Param("system") UniSystem system, @Param("isDefault") Boolean isDefault);

    @Modifying
    @Query("UPDATE Role r SET r.priority = :priority WHERE r.id = :roleId")
    int updatePriority(@Param("roleId") UUID roleId, @Param("priority") Integer priority);

    // Join Fetch Queries
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :roleId")
    Optional<Role> findByIdWithPermissions(@Param("roleId") UUID roleId);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions rp LEFT JOIN FETCH rp.permission WHERE r.id = :roleId")
    Optional<Role> findByIdWithPermissionsDetails(@Param("roleId") UUID roleId);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.id = :roleId")
    Optional<Role> findByIdWithUsers(@Param("roleId") UUID roleId);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.children WHERE r.id = :roleId")
    Optional<Role> findByIdWithChildren(@Param("roleId") UUID roleId);

    // UniSystem Statistics
    @Query("SELECT COUNT(r) FROM Role r WHERE r.uniSystem.id = :systemId")
    long countBySystemId(@Param("systemId") UUID systemId);

    @Query("SELECT r.uniSystem.id, COUNT(r) FROM Role r GROUP BY r.uniSystem.id")
    List<Object[]> countRolesByUniSystem();
}