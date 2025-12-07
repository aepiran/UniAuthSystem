package ai.uniauth.model.mapper;

import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.Permission;
import ai.uniauth.model.entity.Role;
import ai.uniauth.model.entity.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",
        uses = {PermissionMapper.class, SystemMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roleCode", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntity(RoleUpdateDTO dto, @MappingTarget Role role);

    default String mapSystemCode(Role role) {
        return role.getSystem() != null ? role.getSystem().getSystemCode() : null;
    }

    default String mapSystemName(Role role) {
        return role.getSystem() != null ? role.getSystem().getSystemName() : null;
    }

    default Set<SimplePermissionDTO> mapPermissionsToSimpleDTO(Set<Permission> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
                .map(permission -> new SimplePermissionDTO(
                        permission.getId(),
                        permission.getPermissionCode(),
                        permission.getPermissionName(),
                        permission.getResourceType(),
                        permission.getAction()
                ))
                .collect(Collectors.toSet());
    }

    default Set<SimpleUserDTO> mapUsersToSimpleDTO(Set<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(user -> new SimpleUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName()
                ))
                .collect(Collectors.toSet());
    }
}