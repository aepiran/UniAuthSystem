package ai.uniauth.service;

import ai.uniauth.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface SystemService {

    // System Registration & Management
    SystemRegistrationResponseDTO registerSystem(SystemCreateDTO systemCreateDTO);

    UniSystemDTO getSystemById(Long id);

    UniSystemDTO getSystemByCode(String systemCode);

    Page<UniSystemDTO> getAllSystems(Pageable pageable);

    Page<UniSystemDTO> getActiveSystems(Pageable pageable);

    UniSystemDTO updateSystem(Long id, SystemUpdateDTO systemUpdateDTO);

    void deleteSystem(Long id);

    void activateSystem(Long id);

    void deactivateSystem(Long id);

    // API Key Management
    SystemApiKeyResponseDTO regenerateApiKeys(Long systemId);

    SystemApiKeyResponseDTO regenerateApiKeysByCode(String systemCode);

    boolean validateApiKey(String systemCode, String apiKey);

    void revokeApiKey(Long systemId);

    // System Operations
    SystemStatusDTO getSystemStatus(String systemCode);

    List<SystemStatusDTO> getAllSystemsStatus();

    void syncSystemData(SystemSyncRequestDTO syncRequest);

    // User Management for System
    void addUsersToSystem(Long systemId, Set<Long> userIds);

    void removeUsersFromSystem(Long systemId, Set<Long> userIds);

    Page<UserDTO> getSystemUsers(Long systemId, Pageable pageable);

    // Role Management for System
    Page<RoleDTO> getSystemRoles(Long systemId, Pageable pageable);

    // Permission Management for System
    Page<PermissionDTO> getSystemPermissions(Long systemId, Pageable pageable);

    // Statistics
    SystemStatsDTO getSystemStatistics(String systemCode);

    List<SystemActivityDTO> getSystemActivity(String systemCode, int days);

    // Validation
    boolean systemExists(String systemCode);

    boolean isSystemActive(String systemCode);

    boolean validateSystemAccess(String systemCode, String apiKey);
}

