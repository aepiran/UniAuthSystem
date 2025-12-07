package ai.uniauth.service.mapper;

import ai.uniauth.models.Role;
import ai.uniauth.service.dto.RoleDTO;
import ai.uniauth.service.dto.request.CreateRoleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "userCount", ignore = true)
    RoleDTO toDTO(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Role toEntity(CreateRoleRequest request);
}