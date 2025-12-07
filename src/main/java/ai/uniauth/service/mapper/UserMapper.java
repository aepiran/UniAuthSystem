package ai.uniauth.service.mapper;

import ai.uniauth.models.User;
import ai.uniauth.service.dto.UserDTO;
import ai.uniauth.service.dto.request.CreateUserRequest;
import ai.uniauth.service.dto.request.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "passwordSalt", ignore = true)
    @Mapping(target = "isLocked", constant = "false")
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "mfaEnabled", constant = "false")
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "lastPasswordChange", ignore = true)
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "mustChangePassword", constant = "false")
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "loginAttempts", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "passwordSalt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isLocked", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "lastPasswordChange", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "mustChangePassword", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "loginAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}