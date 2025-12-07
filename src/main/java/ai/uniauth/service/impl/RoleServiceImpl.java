package ai.uniauth.service.impl;

import ai.uniauth.exception.BusinessException;
import ai.uniauth.exception.ResourceAlreadyExistsException;
import ai.uniauth.exception.ResourceNotFoundException;
import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.Permission;
import ai.uniauth.model.entity.Role;
import ai.uniauth.model.entity.UniSystem;
import ai.uniauth.model.mapper.RoleMapper;
import ai.uniauth.rep.*;
import ai.uniauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final SystemRepository systemRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO createRole(RoleCreateDTO roleCreateDTO) {
        // Validate
        if (roleRepository.existsByRoleCode(roleCreateDTO.getRoleCode())) {
            throw new ResourceAlreadyExistsException("Role code already exists: " + roleCreateDTO.getRoleCode());
        }

        // Find system
        UniSystem system = systemRepository.findBySystemCode(roleCreateDTO.getSystemCode())
                .orElseThrow(() -> new ResourceNotFoundException("System not found: " + roleCreateDTO.getSystemCode()));

        // Map to entity
        Role role = roleMapper.toEntity(roleCreateDTO);
        role.setSystem(system);

        // Assign permissions
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(roleCreateDTO.getPermissionIds()));
        if (permissions.size() != roleCreateDTO.getPermissionIds().size()) {
            throw new ResourceNotFoundException("Some permissions not found");
        }

        // Check permissions belong to same system
        for (Permission permission : permissions) {
            if (!permission.getSystem().getId().equals(system.getId())) {
                throw new BusinessException("Permission does not belong to system: " + permission.getPermissionCode());
            }
        }

        role.setPermissions(permissions);

        // Save
        Role savedRole = roleRepository.save(role);
        log.info("Role created with ID: {} and code: {}", savedRole.getId(), savedRole.getRoleCode());

        return roleMapper.toDTO(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
        return roleMapper.toDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));
        return roleMapper.toDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDTO);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleUpdateDTO roleUpdateDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        // Cannot update system role flag if it's a system role
        if (role.getIsSystemRole() && roleUpdateDTO.getIsSystemRole() != null && !roleUpdateDTO.getIsSystemRole()) {
            throw new BusinessException("Cannot change system role flag for existing system role");
        }

        roleMapper.updateEntity(roleUpdateDTO, role);
        role = roleRepository.save(role);

        log.info("Role updated with ID: {}", id);
        return roleMapper.toDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        // Check if role has users assigned
        if (!role.getUsers().isEmpty()) {
            throw new BusinessException("Cannot delete role that has users assigned");
        }

        // Check if it's a system role
        if (role.getIsSystemRole()) {
            throw new BusinessException("Cannot delete system role");
        }

        // Remove all permission associations
        role.getPermissions().clear();
        roleRepository.save(role);

        roleRepository.delete(role);
        log.info("Role deleted with ID: {}", id);
    }

    @Override
    public void assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            throw new ResourceNotFoundException("Some permissions not found");
        }

        // Check permissions belong to same system
        for (Permission permission : permissions) {
            if (!permission.getSystem().getId().equals(role.getSystem().getId())) {
                throw new BusinessException("Permission does not belong to role's system: " + permission.getPermissionCode());
            }
        }

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);

        log.info("Assigned permissions {} to role {}", permissionIds, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRolesByUser(Long userId) {
        List<Role> roles = roleRepository.findByUserId(userId);
        return roles.stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolePermissions(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));

        return role.getPermissions().stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    // Other methods implementation...


    @Override
    public Page<RoleDTO> getRolesBySystem(String systemCode, Pageable pageable) {
        return null;
    }

    @Override
    public void removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {

    }

    @Override
    public List<RoleDTO> getRolesByUsername(String username) {
        return List.of();
    }

    @Override
    public boolean roleExists(String roleCode) {
        return false;
    }

    @Override
    public boolean isRoleAssignedToUser(Long roleId, Long userId) {
        return false;
    }
}