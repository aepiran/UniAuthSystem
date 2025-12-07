package ai.uniauth.service;

import ai.uniauth.models.Role;
import ai.uniauth.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

    // Basic CRUD Operations
    Role createRole(Role role);
    Role updateRole(UUID roleId, Role role);
    Role getRoleById(UUID roleId);
    Role getRoleByCode(String code);
    void deleteRole(UUID roleId);

    // Role Hierarchy Management
//    Role setParentRole(UUID roleId, UUID parentRoleId);
//    Role removeParentRole(UUID roleId);
//    List<Role> getChildRoles(UUID roleId);
//    List<Role> getAncestorRoles(UUID roleId);
//    List<Role> getDescendantRoles(UUID roleId);
//    boolean isDescendantOf(UUID roleId, UUID parentRoleId);
//
//    // Permission Management
    void assignPermission(UUID roleId, UUID permissionId, String accessLevel);
//    void assignPermissions(UUID roleId, Set<UUID> permissionIds, String accessLevel);
//    void revokePermission(UUID roleId, UUID permissionId);
//    void revokeAllPermissions(UUID roleId);
    Set<String> getRolePermissions(UUID roleId);
    Set<String> getRolePermissionsInherited(UUID roleId);
//    boolean hasPermission(UUID roleId, String permissionCode);
//
//    // User Management
//    void addUserToRole(UUID roleId, UUID userId, String assignedBy);
//    void addUsersToRole(UUID roleId, Set<UUID> userIds, String assignedBy);
//    void removeUserFromRole(UUID roleId, UUID userId);
//    void removeAllUsersFromRole(UUID roleId);
//    List<User> getUsersInRole(UUID roleId);
//    long countUsersInRole(UUID roleId);
//
//    // System Management
//    Role assignToSystem(UUID roleId, UUID systemId);
//    Role removeFromSystem(UUID roleId);
//
//    // Search and Filter
//    Page<Role> searchRoles(String keyword, Pageable pageable);
//    Page<Role> filterRoles(Map<String, Object> filters, Pageable pageable);
    List<Role> getSystemRoles(UUID systemId);
    List<Role> getDefaultRoles();
    List<Role> getSystemRoles();
//
//    // Role Inheritance
//    Set<String> getInheritedPermissions(UUID roleId);
//    Set<String> getAllPermissions(UUID roleId); // Direct + Inherited
//    boolean canInheritFrom(UUID roleId, UUID potentialParentId);
//
//    // Bulk Operations
    void bulkDeleteRoles(Set<UUID> roleIds);
//    void bulkAssignToSystem(Set<UUID> roleIds, UUID systemId);
//    void bulkSetDefault(Set<UUID> roleIds, boolean isDefault);
//
//    // Validation
//    boolean isRoleCodeAvailable(String code);
//    boolean isRoleNameAvailable(String name);
//    boolean canDeleteRole(UUID roleId); // Check if role has users or is system role
//
//    // Statistics
//    long countRoles();
//    long countRolesBySystem(UUID systemId);
//    Map<UUID, Long> countUsersPerRole(Set<UUID> roleIds);
//    Map<String, Long> getRoleStatistics();
//
//    // Export/Import
//    byte[] exportRole(UUID roleId, String format);
//    Role importRole(String roleData, String format, UUID importedBy);
//
//    // Clone Role
//    Role cloneRole(UUID roleId, String newCode, String newName);
//
//    // Role Templates
//    Role createFromTemplate(String templateCode, String code, String name, UUID systemId);
//    List<String> getAvailableTemplates();

    User getCurrentUser();
    UUID getCurrentUserId();
}