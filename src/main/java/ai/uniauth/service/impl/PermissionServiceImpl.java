package ai.uniauth.service.impl;

import ai.uniauth.exception.BusinessException;
import ai.uniauth.exception.ResourceAlreadyExistsException;
import ai.uniauth.exception.ResourceNotFoundException;
import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.*;
import ai.uniauth.model.mapper.PermissionMapper;
import ai.uniauth.rep.*;
import ai.uniauth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SystemRepository systemRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionDTO createPermission(PermissionCreateDTO permissionCreateDTO) {
        // Validate permission code uniqueness
        if (permissionRepository.existsByPermissionCode(permissionCreateDTO.getPermissionCode())) {
            throw ResourceAlreadyExistsException.forPermissionCode(permissionCreateDTO.getPermissionCode());
        }

        // Find system
        UniSystem system = systemRepository.findBySystemCode(permissionCreateDTO.getSystemCode())
                .orElseThrow(() -> ResourceNotFoundException.forSystem(permissionCreateDTO.getSystemCode()));

        // Map to entity
        Permission permission = permissionMapper.toEntity(permissionCreateDTO);
        permission.setSystem(system);

        // Save permission
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created with ID: {} and code: {}",
                savedPermission.getId(), savedPermission.getPermissionCode());

        return permissionMapper.toDTO(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(id));
        return permissionMapper.toDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByCode(String permissionCode) {
        Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(permissionCode));
        return permissionMapper.toDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> getPermissionsBySystem(String systemCode, Pageable pageable) {
        return permissionRepository.findBySystemCode(systemCode, pageable)
                .map(permissionMapper::toDTO);
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionCreateDTO permissionCreateDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(id));

        // Check if trying to change permission code (if different)
        if (!permission.getPermissionCode().equals(permissionCreateDTO.getPermissionCode())) {
            if (permissionRepository.existsByPermissionCode(permissionCreateDTO.getPermissionCode())) {
                throw ResourceAlreadyExistsException.forPermissionCode(permissionCreateDTO.getPermissionCode());
            }
        }

        // Check if system is being changed
        if (!permission.getSystem().getSystemCode().equals(permissionCreateDTO.getSystemCode())) {
            UniSystem newSystem = systemRepository.findBySystemCode(permissionCreateDTO.getSystemCode())
                    .orElseThrow(() -> ResourceNotFoundException.forSystem(permissionCreateDTO.getSystemCode()));
            permission.setSystem(newSystem);
        }

        // Update fields
        permission.setPermissionCode(permissionCreateDTO.getPermissionCode());
        permission.setPermissionName(permissionCreateDTO.getPermissionName());
        permission.setDescription(permissionCreateDTO.getDescription());
        permission.setResourceType(permissionCreateDTO.getResourceType());
        permission.setAction(permissionCreateDTO.getAction());

        Permission updatedPermission = permissionRepository.save(permission);
        log.info("Permission updated with ID: {}", id);

        return permissionMapper.toDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(id));

        // Check if permission is assigned to any roles
        if (!permission.getRoles().isEmpty()) {
            throw new BusinessException(
                    String.format("Cannot delete permission that is assigned to %d roles",
                            permission.getRoles().size()),
                    "PERMISSION_ASSIGNED_TO_ROLES"
            );
        }

        permissionRepository.delete(permission);
        log.info("Permission deleted with ID: {}", id);
    }

    @Override
    public List<PermissionDTO> createPermissions(List<PermissionCreateDTO> permissionCreateDTOs) {
        // Validate all permissions first
        Set<String> permissionCodes = permissionCreateDTOs.stream()
                .map(PermissionCreateDTO::getPermissionCode).collect(Collectors.toSet());


        List<Permission> existingPermissions = permissionRepository.findByPermissionCodes(permissionCodes);
        if (!existingPermissions.isEmpty()) {
            String existingCodes = existingPermissions.stream()
                    .map(Permission::getPermissionCode)
                    .collect(Collectors.joining(", "));
            throw ResourceAlreadyExistsException.forPermissionCode(existingCodes);
        }

        // Group by system for batch processing
        Map<String, List<PermissionCreateDTO>> permissionsBySystem = permissionCreateDTOs.stream()
                .collect(Collectors.groupingBy(PermissionCreateDTO::getSystemCode));

        List<Permission> savedPermissions = new ArrayList<>();

        for (Map.Entry<String, List<PermissionCreateDTO>> entry : permissionsBySystem.entrySet()) {
            String systemCode = entry.getKey();
            UniSystem system = systemRepository.findBySystemCode(systemCode)
                    .orElseThrow(() -> ResourceNotFoundException.forSystem(systemCode));

            List<Permission> permissions = entry.getValue().stream()
                    .map(dto -> {
                        Permission permission = permissionMapper.toEntity(dto);
                        permission.setSystem(system);
                        return permission;
                    })
                    .collect(Collectors.toList());

            savedPermissions.addAll(permissionRepository.saveAll(permissions));
        }

        log.info("Created {} permissions in batch", savedPermissions.size());
        return savedPermissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByRole(Long roleId) {
        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByRoleCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> ResourceNotFoundException.forRole(roleCode));
        return getPermissionsByRole(role.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getPermissionCodesByRole(Long roleId) {
        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.forUser(userId));

        Set<Permission> userPermissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            userPermissions.addAll(role.getPermissions());
        }

        return userPermissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByUsername(String username) {
        List<Permission> permissions = permissionRepository.findByUsername(username);
        return permissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getPermissionCodesByUsername(String username) {
        List<Permission> permissions = permissionRepository.findByUsername(username);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.forUser(userId));

        for (Role role : user.getRoles()) {
            boolean hasPermission = role.getPermissions().stream()
                    .anyMatch(p -> p.getPermissionCode().equals(permissionCode));
            if (hasPermission) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String username, String permissionCode) {
        List<Permission> permissions = permissionRepository.findByUsername(username);
        return permissions.stream()
                .anyMatch(p -> p.getPermissionCode().equals(permissionCode));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(Long userId, Set<String> permissionCodes) {
        Set<String> userPermissions = getPermissionCodesByUsername(
                userRepository.findById(userId)
                        .orElseThrow(() -> ResourceNotFoundException.forUser(userId))
                        .getUsername()
        );

        return permissionCodes.stream()
                .anyMatch(userPermissions::contains);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(Long userId, Set<String> permissionCodes) {
        Set<String> userPermissions = getPermissionCodesByUsername(
                userRepository.findById(userId)
                        .orElseThrow(() -> ResourceNotFoundException.forUser(userId))
                        .getUsername()
        );

        return userPermissions.containsAll(permissionCodes);
    }

    @Override
    public void assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ResourceNotFoundException.forRole(roleId));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            throw ResourceNotFoundException.forPermission(permissionIds.toString());
        }

        // Check if all permissions belong to the same system as the role
        Long roleSystemId = role.getSystem().getId();
        for (Permission permission : permissions) {
            if (!permission.getSystem().getId().equals(roleSystemId)) {
                throw new BusinessException(
                        String.format("Permission '%s' does not belong to role's system",
                                permission.getPermissionCode()),
                        "PERMISSION_SYSTEM_MISMATCH"
                );
            }
        }

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);

        log.info("Assigned {} permissions to role {}", permissionIds.size(), roleId);
    }

    @Override
    public void removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ResourceNotFoundException.forRole(roleId));

        Set<Permission> permissionsToRemove = role.getPermissions().stream()
                .filter(p -> permissionIds.contains(p.getId()))
                .collect(Collectors.toSet());

        if (permissionsToRemove.size() != permissionIds.size()) {
            // Some permissions not found in this role
            Set<Long> foundIds = permissionsToRemove.stream()
                    .map(Permission::getId)
                    .collect(Collectors.toSet());
            permissionIds.removeAll(foundIds);
            throw ResourceNotFoundException.forPermission(permissionIds.toString());
        }

        role.getPermissions().removeAll(permissionsToRemove);
        roleRepository.save(role);

        log.info("Removed {} permissions from role {}", permissionIds.size(), roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> getPermissionsBySystemAndResourceType(String systemCode, String resourceType, Pageable pageable) {
        return permissionRepository.findBySystemAndResourceType(systemCode, resourceType, pageable)
                .map(permissionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByUserAndSystem(Long userId, String systemCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.forUser(userId));

        Set<Permission> userPermissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            if (role.getSystem().getSystemCode().equals(systemCode)) {
                userPermissions.addAll(role.getPermissions());
            }
        }

        return userPermissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getPermissionCodesByUserAndSystem(Long userId, String systemCode) {
        List<PermissionDTO> permissions = getPermissionsByUserAndSystem(userId, systemCode);
        return permissions.stream()
                .map(PermissionDTO::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> searchPermissions(String keyword, Pageable pageable) {
        return permissionRepository.findAll(
                (root, query, cb) -> {
                    if (!StringUtils.hasText(keyword)) {
                        return cb.conjunction();
                    }
                    String likePattern = "%" + keyword.toLowerCase() + "%";
                    return cb.or(
                            cb.like(cb.lower(root.get("permissionCode")), likePattern),
                            cb.like(cb.lower(root.get("permissionName")), likePattern),
                            cb.like(cb.lower(root.get("description")), likePattern),
                            cb.like(cb.lower(root.get("resourceType")), likePattern)
                    );
                },
                pageable
        ).map(permissionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDTO> filterPermissions(PermissionFilterDTO filter, Pageable pageable) {
        Specification<Permission> spec = Specification.where(null);

        if (StringUtils.hasText(filter.getSystemCode())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("system").get("systemCode"), filter.getSystemCode()));
        }

        if (StringUtils.hasText(filter.getResourceType())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("resourceType"), filter.getResourceType()));
        }

        if (StringUtils.hasText(filter.getAction())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("action"), filter.getAction()));
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String likePattern = "%" + filter.getKeyword().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("permissionCode")), likePattern),
                            cb.like(cb.lower(root.get("permissionName")), likePattern),
                            cb.like(cb.lower(root.get("description")), likePattern)
                    ));
        }

        if (filter.getAssignedToRole() != null && filter.getAssignedToRole()) {
            spec = spec.and((root, query, cb) -> {
                Join<Permission, Role> roles = root.join("roles", JoinType.INNER);
                return cb.isNotNull(roles.get("id"));
            });
        }

        if (filter.getRoleId() != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Permission, Role> roles = root.join("roles");
                return cb.equal(roles.get("id"), filter.getRoleId());
            });
        }

        if (filter.getCreatedFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedFrom()));
        }

        if (filter.getCreatedTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedTo()));
        }

        return permissionRepository.findAll(spec, pageable)
                .map(permissionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctResourceTypes(String systemCode) {
        return permissionRepository.findBySystemCode(systemCode).stream()
                .map(Permission::getResourceType)
                .distinct()
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctActions(String systemCode, String resourceType) {
        return permissionRepository.findBySystemAndResourceType(systemCode, resourceType, Pageable.unpaged())
                .map(Permission::getAction)
                .stream().collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> createCRUDPermissions(String systemCode, String resourceName) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> ResourceNotFoundException.forSystem(systemCode));

        String resourceUpper = resourceName.toUpperCase();
        List<PermissionCreateDTO> crudPermissions = Arrays.asList(
                new PermissionCreateDTO(
                        resourceUpper + "_CREATE",
                        "Create " + resourceName,
                        "Permission to create " + resourceName,
                        resourceUpper,
                        "CREATE",
                        systemCode
                ),
                new PermissionCreateDTO(
                        resourceUpper + "_READ",
                        "Read " + resourceName,
                        "Permission to read " + resourceName,
                        resourceUpper,
                        "READ",
                        systemCode
                ),
                new PermissionCreateDTO(
                        resourceUpper + "_UPDATE",
                        "Update " + resourceName,
                        "Permission to update " + resourceName,
                        resourceUpper,
                        "UPDATE",
                        systemCode
                ),
                new PermissionCreateDTO(
                        resourceUpper + "_DELETE",
                        "Delete " + resourceName,
                        "Permission to delete " + resourceName,
                        resourceUpper,
                        "DELETE",
                        systemCode
                )
        );

        return createPermissions(crudPermissions);
    }

    @Override
    public List<PermissionDTO> createModulePermissions(String systemCode, String moduleName, Set<String> actions) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> ResourceNotFoundException.forSystem(systemCode));

        String moduleUpper = moduleName.toUpperCase();
        List<PermissionCreateDTO> modulePermissions = actions.stream()
                .map(action -> new PermissionCreateDTO(
                        moduleUpper + "_" + action.toUpperCase(),
                        action + " " + moduleName,
                        "Permission to " + action.toLowerCase() + " " + moduleName,
                        moduleUpper,
                        action.toUpperCase(),
                        systemCode
                ))
                .collect(Collectors.toList());

        return createPermissions(modulePermissions);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionStatsDTO getPermissionStatistics(String systemCode) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> ResourceNotFoundException.forSystem(systemCode));

        List<Permission> permissions = permissionRepository.findBySystemCode(systemCode);

        Map<String, Long> permissionsByResourceType = permissions.stream()
                .collect(Collectors.groupingBy(
                        Permission::getResourceType,
                        Collectors.counting()
                ));

        Map<String, Long> permissionsByAction = permissions.stream()
                .collect(Collectors.groupingBy(
                        Permission::getAction,
                        Collectors.counting()
                ));

        Long permissionsAssignedToRoles = permissions.stream()
                .filter(p -> !p.getRoles().isEmpty())
                .count();

        // Get unique permissions assigned to users through roles
        Set<Long> uniquePermissionIds = new HashSet<>();
        for (Permission permission : permissions) {
            if (!permission.getRoles().isEmpty()) {
                for (Role role : permission.getRoles()) {
                    if (!role.getUsers().isEmpty()) {
                        uniquePermissionIds.add(permission.getId());
                        break;
                    }
                }
            }
        }

        LocalDateTime lastPermissionCreated = permissions.stream()
                .map(Permission::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new PermissionStatsDTO(
                systemCode,
                (long) permissions.size(),
                (long) permissions.size(), // Assuming all are active
                permissionsByResourceType,
                permissionsByAction,
                permissionsAssignedToRoles,
                (long) uniquePermissionIds.size(),
                lastPermissionCreated
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPermissionUsageStatistics(String systemCode) {
        List<Permission> permissions = permissionRepository.findBySystemCode(systemCode);

        Map<String, Long> usageStats = new HashMap<>();
        for (Permission permission : permissions) {
            long userCount = permission.getRoles().stream()
                    .flatMap(role -> role.getUsers().stream())
                    .distinct()
                    .count();

            long roleCount = permission.getRoles().size();

            String statKey = permission.getPermissionCode() + " (" + permission.getPermissionName() + ")";
            usageStats.put(statKey + "_USERS", userCount);
            usageStats.put(statKey + "_ROLES", roleCount);
        }

        return usageStats;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean permissionExists(String permissionCode) {
        return permissionRepository.existsByPermissionCode(permissionCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean permissionExistsInSystem(String permissionCode, String systemCode) {
        return permissionRepository.findByPermissionCode(permissionCode)
                .map(permission -> permission.getSystem().getSystemCode().equals(systemCode))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPermissionAssignedToRole(Long permissionId, Long roleId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(permissionId));

        return permission.getRoles().stream()
                .anyMatch(role -> role.getId().equals(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPermissionAssignedToUser(Long permissionId, Long userId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> ResourceNotFoundException.forPermission(permissionId));

        return permission.getRoles().stream()
                .flatMap(role -> role.getUsers().stream())
                .anyMatch(user -> user.getId().equals(userId));
    }


    @Override
    public void deletePermissions(Set<Long> permissionIds) {

    }

    @Override
    public void assignPermissionToRole(Long permissionId, Long roleId) {

    }

    @Override
    public void removePermissionFromRole(Long permissionId, Long roleId) {

    }

    @Override
    public void addChildPermission(Long parentId, Long childId) {

    }

    @Override
    public void removeChildPermission(Long parentId, Long childId) {

    }

    @Override
    public List<PermissionDTO> getChildPermissions(Long permissionId) {
        return List.of();
    }

    @Override
    public List<PermissionDTO> getParentPermissions(Long permissionId) {
        return List.of();
    }

    @Override
    public boolean isChildOf(Long childId, Long parentId) {
        return false;
    }
}