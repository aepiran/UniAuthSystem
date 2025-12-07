package ai.uniauth.model.mapper;


import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.UniSystem;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemMapper {

    UniSystemDTO toDTO(UniSystem system);

    SimpleSystemDTO toSimpleDTO(UniSystem system);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "apiKey", ignore = true)
    @Mapping(target = "secretKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    UniSystem toEntity(SystemCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "systemCode", ignore = true)
    @Mapping(target = "apiKey", ignore = true)
    @Mapping(target = "secretKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntity(SystemUpdateDTO dto, @MappingTarget UniSystem system);

    // Custom mapping methods
    @AfterMapping
    default void enrichDTO(UniSystem system, @MappingTarget UniSystemDTO dto) {
        if (system.getRoles() != null) {
            dto.setRoleCount(system.getRoles().size());
            dto.setRoles(system.getRoles().stream()
                    .map(role -> new SimpleRoleDTO(
                            role.getId(),
                            role.getRoleCode(),
                            role.getRoleName(),
                            role.getSystem().getSystemCode()
                    ))
                    .collect(Collectors.toSet()));
        }

        if (system.getPermissions() != null) {
            dto.setPermissionCount(system.getPermissions().size());
            dto.setPermissions(system.getPermissions().stream()
                    .map(permission -> new SimplePermissionDTO(
                            permission.getId(),
                            permission.getPermissionCode(),
                            permission.getPermissionName(),
                            permission.getResourceType(),
                            permission.getAction()
                    ))
                    .collect(Collectors.toSet()));
        }

        if (system.getUsers() != null) {
            dto.setUserCount(system.getUsers().size());
            dto.setUsers(system.getUsers().stream()
                    .map(user -> new SimpleUserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFullName()
                    ))
                    .collect(Collectors.toSet()));
        }

        // Mask API key for security
        if (dto.getApiKey() != null && dto.getApiKey().length() > 8) {
            dto.setApiKey(dto.getApiKey().substring(0, 8) + "***");
        }
    }
}