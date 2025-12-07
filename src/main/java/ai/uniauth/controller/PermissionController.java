package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing permissions in UniAuth System")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create a new permission")
    @PreAuthorize("hasPermission('PERMISSION_CREATE')")
    public ResponseEntity<PermissionDTO> createPermission(
            @Valid @RequestBody PermissionCreateDTO permissionCreateDTO) {
        PermissionDTO createdPermission = permissionService.createPermission(permissionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple permissions")
    @PreAuthorize("hasPermission('PERMISSION_CREATE')")
    public ResponseEntity<List<PermissionDTO>> createPermissions(
            @Valid @RequestBody List<PermissionCreateDTO> permissionCreateDTOs) {
        List<PermissionDTO> createdPermissions = permissionService.createPermissions(permissionCreateDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermissions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<PermissionDTO> getPermissionById(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/code/{permissionCode}")
    @Operation(summary = "Get permission by code")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<PermissionDTO> getPermissionByCode(
            @Parameter(description = "Permission code") @PathVariable String permissionCode) {
        PermissionDTO permission = permissionService.getPermissionByCode(permissionCode);
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    @Operation(summary = "Get all permissions with pagination")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Page<PermissionDTO>> getAllPermissions(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<PermissionDTO> permissions = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/system/{systemCode}")
    @Operation(summary = "Get permissions by system")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Page<PermissionDTO>> getPermissionsBySystem(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PermissionDTO> permissions = permissionService.getPermissionsBySystem(systemCode, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/system/{systemCode}/resource-type/{resourceType}")
    @Operation(summary = "Get permissions by system and resource type")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Page<PermissionDTO>> getPermissionsBySystemAndResourceType(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @Parameter(description = "Resource type") @PathVariable String resourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PermissionDTO> permissions = permissionService.getPermissionsBySystemAndResourceType(
                systemCode, resourceType, pageable);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission")
    @PreAuthorize("hasPermission('PERMISSION_UPDATE')")
    public ResponseEntity<PermissionDTO> updatePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Valid @RequestBody PermissionCreateDTO permissionCreateDTO) {
        PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionCreateDTO);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission")
    @PreAuthorize("hasPermission('PERMISSION_DELETE')")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete/batch")
    @Operation(summary = "Delete multiple permissions")
    @PreAuthorize("hasPermission('PERMISSION_DELETE')")
    public ResponseEntity<Void> deletePermissions(
            @RequestBody Set<Long> permissionIds) {
        permissionService.deletePermissions(permissionIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "Get permissions by role")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByRole(roleId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/code/{roleCode}")
    @Operation(summary = "Get permissions by role code")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByRoleCode(
            @Parameter(description = "Role code") @PathVariable String roleCode) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByRoleCode(roleCode);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/{roleId}/codes")
    @Operation(summary = "Get permission codes by role")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Set<String>> getPermissionCodesByRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        Set<String> permissionCodes = permissionService.getPermissionCodesByRole(roleId);
        return ResponseEntity.ok(permissionCodes);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get permissions by user")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByUser(userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/username/{username}")
    @Operation(summary = "Get permissions by username")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#username)")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByUsername(username);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/{userId}/system/{systemCode}")
    @Operation(summary = "Get permissions by user and system")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByUserAndSystem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "System code") @PathVariable String systemCode) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByUserAndSystem(userId, systemCode);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/{userId}/system/{systemCode}/codes")
    @Operation(summary = "Get permission codes by user and system")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Set<String>> getPermissionCodesByUserAndSystem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "System code") @PathVariable String systemCode) {
        Set<String> permissionCodes = permissionService.getPermissionCodesByUserAndSystem(userId, systemCode);
        return ResponseEntity.ok(permissionCodes);
    }

    @PostMapping("/check/user/{userId}")
    @Operation(summary = "Check if user has permission")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> hasPermission(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam String permissionCode) {
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping("/check/user/{userId}/any")
    @Operation(summary = "Check if user has any of the permissions")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> hasAnyPermission(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody Set<String> permissionCodes) {
        boolean hasAny = permissionService.hasAnyPermission(userId, permissionCodes);
        return ResponseEntity.ok(hasAny);
    }

    @PostMapping("/check/user/{userId}/all")
    @Operation(summary = "Check if user has all permissions")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> hasAllPermissions(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody Set<String> permissionCodes) {
        boolean hasAll = permissionService.hasAllPermissions(userId, permissionCodes);
        return ResponseEntity.ok(hasAll);
    }

    @PostMapping("/check/username/{username}")
    @Operation(summary = "Check if username has permission")
    @PreAuthorize("hasPermission('PERMISSION_READ') or @securityService.isCurrentUser(#username)")
    public ResponseEntity<Boolean> checkPermissionByUsername(
            @Parameter(description = "Username") @PathVariable String username,
            @RequestParam String permissionCode) {
        boolean hasPermission = permissionService.hasPermission(username, permissionCode);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping("/search")
    @Operation(summary = "Search permissions by keyword")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Page<PermissionDTO>> searchPermissions(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PermissionDTO> permissions = permissionService.searchPermissions(keyword, pageable);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter permissions with criteria")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Page<PermissionDTO>> filterPermissions(
            @RequestBody PermissionFilterDTO filter,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PermissionDTO> permissions = permissionService.filterPermissions(filter, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/system/{systemCode}/resource-types")
    @Operation(summary = "Get distinct resource types in a system")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<List<String>> getDistinctResourceTypes(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        List<String> resourceTypes = permissionService.getDistinctResourceTypes(systemCode);
        return ResponseEntity.ok(resourceTypes);
    }

    @GetMapping("/system/{systemCode}/resource-type/{resourceType}/actions")
    @Operation(summary = "Get distinct actions for a resource type")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<List<String>> getDistinctActions(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @Parameter(description = "Resource type") @PathVariable String resourceType) {
        List<String> actions = permissionService.getDistinctActions(systemCode, resourceType);
        return ResponseEntity.ok(actions);
    }

    @PostMapping("/templates/crud")
    @Operation(summary = "Create CRUD permissions for a resource")
    @PreAuthorize("hasPermission('PERMISSION_CREATE')")
    public ResponseEntity<List<PermissionDTO>> createCRUDPermissions(
            @RequestParam String systemCode,
            @RequestParam String resourceName) {
        List<PermissionDTO> permissions = permissionService.createCRUDPermissions(systemCode, resourceName);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissions);
    }

    @PostMapping("/templates/module")
    @Operation(summary = "Create module permissions")
    @PreAuthorize("hasPermission('PERMISSION_CREATE')")
    public ResponseEntity<List<PermissionDTO>> createModulePermissions(
            @RequestParam String systemCode,
            @RequestParam String moduleName,
            @RequestBody Set<String> actions) {
        List<PermissionDTO> permissions = permissionService.createModulePermissions(systemCode, moduleName, actions);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissions);
    }

    @GetMapping("/system/{systemCode}/statistics")
    @Operation(summary = "Get permission statistics for a system")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<PermissionStatsDTO> getPermissionStatistics(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        PermissionStatsDTO stats = permissionService.getPermissionStatistics(systemCode);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/system/{systemCode}/usage")
    @Operation(summary = "Get permission usage statistics")
    @PreAuthorize("hasPermission('PERMISSION_READ')")
    public ResponseEntity<Map<String, Long>> getPermissionUsageStatistics(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        Map<String, Long> usageStats = permissionService.getPermissionUsageStatistics(systemCode);
        return ResponseEntity.ok(usageStats);
    }
}