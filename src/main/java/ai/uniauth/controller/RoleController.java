package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.RoleService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing roles in UniAuth System")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role")
    @PreAuthorize("hasPermission('ROLE_CREATE')")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
        RoleDTO createdRole = roleService.createRole(roleCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/code/{roleCode}")
    @Operation(summary = "Get role by code")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<RoleDTO> getRoleByCode(
            @Parameter(description = "Role code") @PathVariable String roleCode) {
        RoleDTO role = roleService.getRoleByCode(roleCode);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @Operation(summary = "Get all roles with pagination")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<Page<RoleDTO>> getAllRoles(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<RoleDTO> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/system/{systemCode}")
    @Operation(summary = "Get roles by system")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<Page<RoleDTO>> getRolesBySystem(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RoleDTO> roles = roleService.getRolesBySystem(systemCode, pageable);
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    @PreAuthorize("hasPermission('ROLE_UPDATE')")
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO roleUpdateDTO) {
        RoleDTO updatedRole = roleService.updateRole(id, roleUpdateDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    @PreAuthorize("hasPermission('ROLE_DELETE')")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions/assign")
    @Operation(summary = "Assign permissions to role")
    @PreAuthorize("hasPermission('ROLE_UPDATE')")
    public ResponseEntity<Void> assignPermissionsToRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId,
            @RequestBody Set<Long> permissionIds) {
        roleService.assignPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/remove")
    @Operation(summary = "Remove permissions from role")
    @PreAuthorize("hasPermission('ROLE_UPDATE')")
    public ResponseEntity<Void> removePermissionsFromRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId,
            @RequestBody Set<Long> permissionIds) {
        roleService.removePermissionsFromRole(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get roles by user")
    @PreAuthorize("hasPermission('ROLE_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<List<RoleDTO>> getRolesByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<RoleDTO> roles = roleService.getRolesByUser(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/user/username/{username}")
    @Operation(summary = "Get roles by username")
    @PreAuthorize("hasPermission('ROLE_READ') or @securityService.isCurrentUser(#username)")
    public ResponseEntity<List<RoleDTO>> getRolesByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        List<RoleDTO> roles = roleService.getRolesByUsername(username);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "Get permissions by role")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<Set<String>> getRolePermissions(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        Set<String> permissions = roleService.getRolePermissions(roleId.toString());
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/exists/{roleCode}")
    @Operation(summary = "Check if role exists")
    @PreAuthorize("hasPermission('ROLE_READ')")
    public ResponseEntity<Boolean> roleExists(
            @Parameter(description = "Role code") @PathVariable String roleCode) {
        boolean exists = roleService.roleExists(roleCode);
        return ResponseEntity.ok(exists);
    }
}