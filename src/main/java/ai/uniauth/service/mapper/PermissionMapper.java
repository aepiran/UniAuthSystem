package ai.uniauth.service.mapper;

import ai.uniauth.models.Permission;
import ai.uniauth.service.dto.PermissionDTO;
import ai.uniauth.service.dto.request.CreatePermissionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "roleCount", ignore = true)
    PermissionDTO toDTO(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Permission toEntity(CreatePermissionRequest request);
}