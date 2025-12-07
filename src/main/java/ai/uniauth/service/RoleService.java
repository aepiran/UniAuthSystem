package ai.uniauth.service;

import ai.uniauth.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

public interface RoleService {

    RoleDTO createRole(RoleCreateDTO roleCreateDTO);
    RoleDTO getRoleById(Long id);
    RoleDTO getRoleByCode(String roleCode);
    Page<RoleDTO> getAllRoles(Pageable pageable);
    Page<RoleDTO> getRolesBySystem(String systemCode, Pageable pageable);
    RoleDTO updateRole(Long id, RoleUpdateDTO roleUpdateDTO);
    void deleteRole(Long id);

    void assignPermissionsToRole(Long roleId, Set<Long> permissionIds);
    void removePermissionsFromRole(Long roleId, Set<Long> permissionIds);

    List<RoleDTO> getRolesByUser(Long userId);
    List<RoleDTO> getRolesByUsername(String username);

    boolean roleExists(String roleCode);
    boolean isRoleAssignedToUser(Long roleId, Long userId);

    Set<String> getRolePermissions(String roleCode);
}