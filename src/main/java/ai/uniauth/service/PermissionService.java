package ai.uniauth.service;


import ai.uniauth.model.dto.PermissionCreateDTO;
import ai.uniauth.model.dto.PermissionDTO;
import ai.uniauth.model.dto.PermissionFilterDTO;
import ai.uniauth.model.dto.PermissionStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionService {

    // CRUD Operations
    PermissionDTO createPermission(PermissionCreateDTO permissionCreateDTO);

    PermissionDTO getPermissionById(Long id);

    PermissionDTO getPermissionByCode(String permissionCode);

    Page<PermissionDTO> getAllPermissions(Pageable pageable);

    Page<PermissionDTO> getPermissionsBySystem(String systemCode, Pageable pageable);

    PermissionDTO updatePermission(Long id, PermissionCreateDTO permissionCreateDTO);

    void deletePermission(Long id);

    // Batch Operations
    List<PermissionDTO> createPermissions(List<PermissionCreateDTO> permissionCreateDTOs);

    void deletePermissions(Set<Long> permissionIds);

    // System-specific Operations
    Page<PermissionDTO> getPermissionsBySystemAndResourceType(String systemCode, String resourceType, Pageable pageable);

    List<PermissionDTO> getPermissionsByRole(Long roleId);

    List<PermissionDTO> getPermissionsByRoleCode(String roleCode);

    Set<String> getPermissionCodesByRole(Long roleId);

    // User Permission Operations
    List<PermissionDTO> getPermissionsByUser(Long userId);

    List<PermissionDTO> getPermissionsByUsername(String username);

    Set<String> getPermissionCodesByUsername(String username);

    List<PermissionDTO> getPermissionsByUserAndSystem(Long userId, String systemCode);

    Set<String> getPermissionCodesByUserAndSystem(Long userId, String systemCode);

    // Permission Checking
    boolean hasPermission(Long userId, String permissionCode);

    boolean hasPermission(String username, String permissionCode);

    boolean hasAnyPermission(Long userId, Set<String> permissionCodes);

    boolean hasAllPermissions(Long userId, Set<String> permissionCodes);

    // Permission Assignment Management
    void assignPermissionToRole(Long permissionId, Long roleId);

    void assignPermissionsToRole(Long roleId, Set<Long> permissionIds);

    void removePermissionFromRole(Long permissionId, Long roleId);

    void removePermissionsFromRole(Long roleId, Set<Long> permissionIds);

    // Permission Hierarchy (Optional)
    void addChildPermission(Long parentId, Long childId);

    void removeChildPermission(Long parentId, Long childId);

    List<PermissionDTO> getChildPermissions(Long permissionId);

    List<PermissionDTO> getParentPermissions(Long permissionId);

    boolean isChildOf(Long childId, Long parentId);

    // Permission Validation
    boolean permissionExists(String permissionCode);

    boolean permissionExistsInSystem(String permissionCode, String systemCode);

    boolean isPermissionAssignedToRole(Long permissionId, Long roleId);

    boolean isPermissionAssignedToUser(Long permissionId, Long userId);

    // Permission Templates (For quick creation)
    List<PermissionDTO> createCRUDPermissions(String systemCode, String resourceName);

    List<PermissionDTO> createModulePermissions(String systemCode, String moduleName, Set<String> actions);

    // Search and Filter
    Page<PermissionDTO> searchPermissions(String keyword, Pageable pageable);

    Page<PermissionDTO> filterPermissions(PermissionFilterDTO filter, Pageable pageable);

    List<String> getDistinctResourceTypes(String systemCode);

    List<String> getDistinctActions(String systemCode, String resourceType);

    // Statistics
    PermissionStatsDTO getPermissionStatistics(String systemCode);

    Map<String, Long> getPermissionUsageStatistics(String systemCode);
}
