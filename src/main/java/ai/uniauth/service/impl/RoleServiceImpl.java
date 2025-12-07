package ai.uniauth.service.impl;

import ai.uniauth.models.Permission;
import ai.uniauth.models.Role;
import ai.uniauth.models.RolePermission;
import ai.uniauth.models.User;
import ai.uniauth.rep.PermissionRep;
import ai.uniauth.rep.RoleRep;
import ai.uniauth.rep.RolePermissionRep;
import ai.uniauth.rep.UserRoleRep;
import ai.uniauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InterruptedIOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRep roleRep;
    private final RolePermissionRep rolePermissionRep;
    private final PermissionRep permissionRep;
    private final UserRoleRep userRoleRep;
//    private final AuditService auditService;

    @Override
    @Transactional
    public Role createRole(Role role) {
        log.info("Creating new role: {}", role.getCode());

        // Validate unique constraints
        if (roleRep.existsByCode(role.getCode())) {
            throw new IllegalArgumentException("Role code already exists: " + role.getCode());
        }
        if (roleRep.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + role.getName());
        }

        // Set default values
        if (role.getIsSystemRole() == null) {
            role.setIsSystemRole(false);
        }
        if (role.getIsDefault() == null) {
            role.setIsDefault(false);
        }
        if (role.getPriority() == null) {
            role.setPriority(0);
        }

        role.setCreatedAt(LocalDateTime.now());

        Role savedRole = roleRep.save(role);

        // If this is a default role, update other default roles
        if (role.getIsDefault() && role.getUniSystem() != null) {
            roleRep.updateDefaultRolesByUniSystem(role.getUniSystem(), false);
        }

        // Audit log
//        auditService.logRoleCreation(getCurrentUserId(), savedRole);

        log.info("Role created successfully: {}", savedRole.getCode());
        return savedRole;
    }

    @Override
    @Transactional
    public Role updateRole(UUID roleId, Role updates) {
        log.info("Updating role: {}", roleId);

        Role role = getRoleById(roleId);

        // Check if role can be modified
        if (role.getIsSystemRole()) {
            throw new IllegalArgumentException("System roles cannot be modified");
        }

        // Update fields
        if (updates.getName() != null && !updates.getName().equals(role.getName())) {
            if (roleRep.existsByName(updates.getName())) {
                throw new IllegalArgumentException("Role name already exists: " + updates.getName());
            }
            role.setName(updates.getName());
        }

        if (updates.getDescription() != null) {
            role.setDescription(updates.getDescription());
        }

        if (updates.getPriority() != null) {
            role.setPriority(updates.getPriority());
        }

        if (updates.getIsDefault() != null && !updates.getIsDefault().equals(role.getIsDefault())) {
            role.setIsDefault(updates.getIsDefault());
            if (updates.getIsDefault() && role.getUniSystem() != null) {
                roleRep.updateDefaultRolesByUniSystem(role.getUniSystem(), false);
            }
        }

        role.setUpdatedAt(LocalDateTime.now());
        role.setUpdatedBy(getCurrentUser().getUsername());

        Role updatedRole = roleRep.save(role);

        // Audit log
//        auditService.logRoleUpdate(getCurrentUserId(), role, updates);

        log.info("Role updated successfully: {}", roleId);
        return updatedRole;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(UUID roleId) {
        return roleRep.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByCode(String code) {
        return roleRep.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + code));
    }

    @Override
    @Transactional
    public void deleteRole(UUID roleId) {
        log.info("Deleting role: {}", roleId);

        Role role = getRoleById(roleId);

        // Check if role can be deleted
        if (role.getIsSystemRole()) {
            throw new IllegalArgumentException("System roles cannot be deleted");
        }

        if (userRoleRep.countByRoleId(roleId) > 0) {
            throw new IllegalArgumentException("Role has assigned users and cannot be deleted");
        }

        // Remove all permissions from role
        rolePermissionRep.deleteByRoleId(roleId);

        // Remove role from hierarchy
        if (role.getParent() != null) {
            role.setParent(null);
        }

        // Update children to remove parent reference
        for (Role child : role.getChildren()) {
            child.setParent(null);
            roleRep.save(child);
        }

        // Soft delete
        role.setIsDeleted(true);
        role.setDeletedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        role.setUpdatedBy(getCurrentUser().getUsername());

        roleRep.save(role);

        // Audit log
//        auditService.logRoleDeletion(getCurrentUserId(), role);

        log.info("Role deleted successfully: {}", roleId);
    }

    @Override
    @Transactional
    public void assignPermission(UUID roleId, UUID permissionId, String accessLevel) {
        Role role = getRoleById(roleId);

        // Check if permission already assigned
        if (rolePermissionRep.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new IllegalArgumentException("Permission already assigned to role");
        }

        Permission permission = permissionRep.findById(permissionId).orElseThrow(IllegalArgumentException::new);

        // Assign permission
        rolePermissionRep.save(RolePermission.builder()
                .role(role)
                .permission(permission)
                .accessLevel(accessLevel)
                .assignedAt(LocalDateTime.now())
                .assignedBy(getCurrentUser().getUsername())
                .build());

        // Audit log
//        auditService.logPermissionAssignment(getCurrentUserId(), role, permissionId, accessLevel);

        log.info("Permission {} assigned to role {} with access level {}",
                permissionId, roleId, accessLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolePermissions(UUID roleId) {
        return rolePermissionRep.findPermissionCodesByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolePermissionsInherited(UUID roleId) {
        Set<String> allPermissions = new HashSet<>(getRolePermissions(roleId));

        // Get permissions from parent roles
        Role role = getRoleById(roleId);
        if (role.getParent() != null) {
            allPermissions.addAll(getRolePermissionsInherited(role.getParent().getId()));
        }

        return allPermissions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getSystemRoles(UUID systemId) {
        return roleRep.findByUniSystemId(systemId);
    }

    @Override
    public List<Role> getDefaultRoles() {
        return roleRep.findByIsDefaultTrue();
    }

    @Override
    public List<Role> getSystemRoles() {
        return roleRep.findByIsSystemRoleTrue();
    }

    @Override
    @Transactional
    public void bulkDeleteRoles(Set<UUID> roleIds) {
        for (UUID roleId : roleIds) {
            try {
                deleteRole(roleId);
            } catch (Exception e) {
                log.error("Failed to delete role {}: {}", roleId, e.getMessage());
            }
        }

        log.info("Bulk deletion completed for {} roles", roleIds.size());
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
}