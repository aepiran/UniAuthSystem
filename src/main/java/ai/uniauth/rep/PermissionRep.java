package ai.uniauth.rep;

import ai.uniauth.models.Permission;
import ai.uniauth.models.UniSystem;
import ai.uniauth.models.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRep extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {

    // Basic Finders
    Optional<Permission> findByCode(String code);
    boolean existsByCode(String code);

    // System-based Finders
    List<Permission> findByUniSystem(UniSystem system);
    List<Permission> findByUniSystemId(UUID systemId);
    List<Permission> findByUniSystemCode(String systemCode);

    // Category-based Finders
    List<Permission> findByCategory(String category);
    List<Permission> findByCategoryAndUniSystemCode(String category, String systemCode);
    List<Permission> findBySubcategory(String subcategory);
    List<Permission> findByModule(String module);

    // Risk-based Finders
    List<Permission> findByRiskLevel(RiskLevel riskLevel);
    List<Permission> findByIsSensitiveTrue();
    List<Permission> findByRequiresApprovalTrue();

    // Search Queries
    @Query("SELECT p FROM Permission p WHERE " +
            "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Permission> search(@Param("keyword") String keyword, Pageable pageable);

    // Role-based Finders
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.role.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT DISTINCT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.role.code = :roleCode")
    List<Permission> findByRoleCode(@Param("roleCode") String roleCode);

    // User-based Finders (through roles)
    @Query("SELECT DISTINCT p FROM Permission p " +
            "JOIN p.rolePermissions rp " +
            "JOIN rp.role r " +
            "JOIN r.users u " +
            "WHERE u.id = :userId")
    List<Permission> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT p.code FROM Permission p " +
            "JOIN p.rolePermissions rp " +
            "JOIN rp.role r " +
            "JOIN r.users u " +
            "WHERE u.id = :userId AND rp.accessLevel = 'ALLOW'")
    Set<String> findPermissionCodesByUserId(@Param("userId") UUID userId);

    // Bulk Operations
    @Query("SELECT p FROM Permission p WHERE p.code IN :codes")
    List<Permission> findByCodes(@Param("codes") Set<String> codes);

    @Query("SELECT p FROM Permission p WHERE p.uniSystem.id = :systemId AND p.code IN :codes")
    List<Permission> findByUniSystemIdAndCodes(@Param("systemId") UUID systemId,
                                            @Param("codes") Set<String> codes);

    // Statistics
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.uniSystem.id = :systemId")
    long countByUniSystemId(@Param("systemId") UUID systemId);

    @Query("SELECT p.category, COUNT(p) FROM Permission p WHERE p.uniSystem.id = :systemId GROUP BY p.category")
    List<Object[]> countByCategoryAndUniSystem(@Param("systemId") UUID systemId);

    @Query("SELECT p.riskLevel, COUNT(p) FROM Permission p GROUP BY p.riskLevel")
    List<Object[]> countByRiskLevel();

    // Join Fetch Queries
    @Query("SELECT p FROM Permission p LEFT JOIN FETCH p.rolePermissions WHERE p.id = :permissionId")
    Optional<Permission> findByIdWithRolePermissions(@Param("permissionId") UUID permissionId);

    // Unused Permissions
    @Query("SELECT p FROM Permission p WHERE NOT EXISTS (" +
            "SELECT rp FROM RolePermission rp WHERE rp.permission.id = p.id)")
    List<Permission> findUnusedPermissions();
}