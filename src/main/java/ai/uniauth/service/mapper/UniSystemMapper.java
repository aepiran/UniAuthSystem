package ai.uniauth.service.mapper;

import ai.uniauth.models.UniSystem;
import ai.uniauth.service.dto.UniSystemDTO;
import ai.uniauth.service.dto.request.CreateSystemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UniSystemMapper {

    @Mapping(target = "userCount", ignore = true)
    @Mapping(target = "roleCount", ignore = true)
    @Mapping(target = "permissionCount", ignore = true)
//    @Mapping(target = "isHealthy", ignore = true)
    UniSystemDTO toDTO(UniSystem system);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    @Mapping(target = "lastHealthCheck", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "apiKeys", ignore = true)
    @Mapping(target = "webhookLogs", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    UniSystem toEntity(CreateSystemRequest request);
}