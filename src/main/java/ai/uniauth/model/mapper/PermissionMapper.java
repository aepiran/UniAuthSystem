package ai.uniauth.model.mapper;

import ai.uniauth.model.dto.PermissionCreateDTO;
import ai.uniauth.model.dto.PermissionDTO;
import ai.uniauth.model.entity.Permission;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        uses = {SystemMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionDTO toDTO(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Permission toEntity(PermissionCreateDTO dto);

    default String mapSystemCode(Permission permission) {
        return permission.getSystem() != null ? permission.getSystem().getSystemCode() : null;
    }
}