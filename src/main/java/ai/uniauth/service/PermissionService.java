package ai.uniauth.service;

import ai.uniauth.models.Permission;
import ai.uniauth.models.Role;
import ai.uniauth.models.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {

    // Basic CRUD Operations
    Permission createPermission(Permission permission);
    Permission updatePermission(UUID permissionId, Permission permission);
    Permission getPermissionById(UUID permissionId);
    Permission getPermissionByCode(String code);
    void deletePermission(UUID permissionId);

    // System Management
    List<Permission> getSystemPermissions(UUID systemId);
    List<Permission> getSystemPermissionsByCode(String systemCode);
    Permission assignToSystem(UUID permissionId, UUID systemId);

    // Category Management
    List<Permission> getPermissionsByCategory(String category);
    List<Permission> getPermissionsByCategoryAndSystem(String category, UUID systemId);
    List<Permission> getPermissionsByModule(String module);
    Set<String> getAllCategories();
    Set<String> getAllModules();

    // Risk Level Management
    List<Permission> getPermissionsByRiskLevel(RiskLevel riskLevel);
    Permission updateRiskLevel(UUID permissionId, RiskLevel riskLevel);
    Permission markAsSensitive(UUID permissionId, boolean sensitive);
    Permission setRequiresApproval(UUID permissionId, boolean requiresApproval);

    // Role Management
    void addPermissionToRole(UUID permissionId, UUID roleId, String accessLevel);
    void removePermissionFromRole(UUID permissionId, UUID roleId);
    List<Role> getRolesWithPermission(UUID permissionId);
    long countRolesWithPermission(UUID permissionId);

    // User Permission Checking
    boolean userHasPermission(UUID userId, String permissionCode);
    boolean userHasAnyPermission(UUID userId, Set<String> permissionCodes);
    boolean userHasAllPermissions(UUID userId, Set<String> permissionCodes);
    Set<String> getUserEffectivePermissions(UUID userId);
    Set<String> getUserDirectPermissions(UUID userId);

    // Search and Filter
    Page<Permission> searchPermissions(String keyword, Pageable pageable);
    Page<Permission> filterPermissions(Map<String, Object> filters, Pageable pageable);
    List<Permission> getPermissionsByCodes(Set<String> codes);
    List<Permission> getPermissionsBySystemAndCodes(UUID systemId, Set<String> codes);

    // Bulk Operations
    void bulkCreatePermissions(List<Permission> permissions);
    void bulkDeletePermissions(Set<UUID> permissionIds);
    void bulkUpdateRiskLevel(Set<UUID> permissionIds, RiskLevel riskLevel);
    void bulkAssignToSystem(Set<UUID> permissionIds, UUID systemId);

    // Validation
    boolean isPermissionCodeAvailable(String code);
    boolean isPermissionInUse(UUID permissionId);
    boolean canDeletePermission(UUID permissionId);

    // Statistics
    long countPermissions();
    long countPermissionsBySystem(UUID systemId);
    long countPermissionsByRiskLevel(RiskLevel riskLevel);
    Map<String, Long> countPermissionsByCategory();
    Map<UUID, Long> countRolesPerPermission(Set<UUID> permissionIds);

    // Permission Hierarchy
    List<Permission> getParentPermissions(UUID permissionId);
    List<Permission> getChildPermissions(UUID permissionId);
    Permission setParentPermission(UUID permissionId, UUID parentId);
    Permission removeParentPermission(UUID permissionId);

    // Import/Export
    byte[] exportPermissions(List<UUID> permissionIds, String format);
    List<Permission> importPermissions(byte[] data, String format, UUID systemId, UUID importedBy);

    // Permission Templates
    List<Permission> createFromTemplate(String templateName, UUID systemId);
    Map<String, List<Permission>> getSystemPermissionTemplates();

    // Audit and Compliance
    List<Permission> getSensitivePermissions();
    List<Permission> getHighRiskPermissions();
    List<Permission> getUnusedPermissions();
    List<Permission> getPermissionsRequiringApproval();

    // Permission Validation
    boolean validatePermissionCode(String code);
    boolean validatePermissionScope(UUID permissionId, Map<String, Object> context);
}