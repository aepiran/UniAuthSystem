package ai.uniauth.model.mapper;

import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.Role;
import ai.uniauth.model.entity.UniSystem;
import ai.uniauth.model.entity.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",
        uses = {RoleMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Entity to DTO
    UserDTO toDTO(User user);

    SimpleUserDTO toSimpleDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "systems", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "systems", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(UserUpdateDTO dto, @MappingTarget User user);

    // Custom mapping methods
    @AfterMapping
    default void enrichDTO(User user, @MappingTarget UserDTO dto) {
        if (user.getSystems() != null) {
            dto.setAssignedSystemCodes(user.getSystems().stream()
                    .map(UniSystem::getSystemCode)
                    .collect(Collectors.toSet()));
        }

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(this::mapToSimpleRoleDTO)
                    .collect(Collectors.toSet()));
        }
    }

    private SimpleRoleDTO mapToSimpleRoleDTO(Role role) {
        if (role == null) return null;
        return new SimpleRoleDTO(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getSystem().getSystemCode()
        );
    }
}