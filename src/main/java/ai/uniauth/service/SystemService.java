package ai.uniauth.service;

import ai.uniauth.models.*;
import ai.uniauth.models.enums.AuthType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface SystemService {

    // Basic CRUD Operations
    UniSystem createSystem(UniSystem system);
    UniSystem updateSystem(UUID systemId, UniSystem system);
    UniSystem getSystemById(UUID systemId);
    UniSystem getSystemByCode(String code);
    void deleteSystem(UUID systemId);

    // Status Management
    UniSystem activateSystem(UUID systemId);
    UniSystem deactivateSystem(UUID systemId);
    UniSystem suspendSystem(UUID systemId);
    UniSystem resumeSystem(UUID systemId);
    boolean isSystemActive(UUID systemId);
    boolean isSystemHealthy(UUID systemId);

    // Configuration Management
    UniSystem updateConfiguration(UUID systemId, Map<String, Object> config);
    Map<String, Object> getConfiguration(UUID systemId);
    UniSystem updateWebhookConfig(UUID systemId, String url, String secret);
    UniSystem updateApiEndpoint(UUID systemId, String endpoint);

    // Authentication Configuration
    UniSystem updateAuthType(UUID systemId, AuthType authType);
    UniSystem updateClientCredentials(UUID systemId, String clientId, String clientSecret);
    UniSystem updateJwtConfig(UUID systemId, String issuer, Long expiration);
    UniSystem updateRateLimit(UUID systemId, Integer rateLimit);

    // Health Check and Monitoring
    boolean checkSystemHealth(UUID systemId);
    UniSystem updateHealthStatus(UUID systemId, boolean healthy, String status);
    UniSystem recordHealthCheck(UUID systemId, boolean success, String details);
    List<UniSystem> getUnhealthySystems();
    List<UniSystem> getSystemsNeedingHealthCheck();

    // Role and Permission Management
    List<Role> getSystemRoles(UUID systemId);
    List<Permission> getSystemPermissions(UUID systemId);
    Role createSystemRole(UUID systemId, Role role);
    Permission createSystemPermission(UUID systemId, Permission permission);
    void importSystemPermissions(UUID systemId, List<Permission> permissions);

    // User Management
    List<User> getSystemUsers(UUID systemId);
    long countSystemUsers(UUID systemId);
    void addUserToSystem(UUID systemId, UUID userId);
    void removeUserFromSystem(UUID systemId, UUID userId);
    boolean isUserInSystem(UUID systemId, UUID userId);

    // API Key Management
    ApiKey createApiKey(UUID systemId, String name, String description, Set<String> permissions);
    List<ApiKey> getSystemApiKeys(UUID systemId);
    void revokeApiKey(UUID apiKeyId);
    ApiKey rotateApiKey(UUID apiKeyId);
    boolean validateApiKey(String apiKey, UUID systemId);

    // Webhook Management
    void sendWebhook(UUID systemId, String eventType, Map<String, Object> payload);
    List<WebhookLog> getWebhookLogs(UUID systemId, LocalDateTime start, LocalDateTime end);
    void retryFailedWebhooks(UUID systemId);

    // Search and Filter
    Page<System> searchSystems(String keyword, Pageable pageable);
    Page<System> filterSystems(Map<String, Object> filters, Pageable pageable);
    List<System> getActiveSystems();
    List<System> getInternalSystems();
    List<System> getSystemsByAuthType(AuthType authType);

    // Integration and Sync
    UniSystem syncSystemData(UUID systemId);
    UniSystem forceSync(UUID systemId);
    UniSystem updateLastSync(UUID systemId);
    List<System> getSystemsNeedingSync();

    // Bulk Operations
    void bulkActivateSystems(Set<UUID> systemIds);
    void bulkDeactivateSystems(Set<UUID> systemIds);
    void bulkDeleteSystems(Set<UUID> systemIds);
    Map<UUID, Boolean> bulkCheckHealth(Set<UUID> systemIds);

    // Validation
    boolean isSystemCodeAvailable(String code);
    boolean canDeleteSystem(UUID systemId);
    boolean validateSystemConfiguration(Map<String, Object> config);

    // Statistics and Reporting
    long countSystems();
    long countActiveSystems();
    Map<AuthType, Long> countSystemsByAuthType();
    Map<String, Long> getSystemStatistics(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getSystemUsageReport(UUID systemId, LocalDateTime start, LocalDateTime end);

    // Registration and Onboarding
    UniSystem registerNewSystem(String name, String code, String baseUrl, AuthType authType);
    UniSystem completeOnboarding(UUID systemId);
    UniSystem sendRegistrationEmail(UUID systemId, String email);

    // Audit and Compliance
    UniSystem updateComplianceConfig(UUID systemId, Map<String, Object> compliance);
    boolean isSystemCompliant(UUID systemId);

    // Backup and Restore
    byte[] backupSystemConfig(UUID systemId);
    UniSystem restoreSystemConfig(UUID systemId, byte[] configData);
    UniSystem cloneSystem(UUID systemId, String newCode, String newName);

    // Maintenance
    UniSystem startMaintenance(UUID systemId, String reason, LocalDateTime endTime);
    UniSystem endMaintenance(UUID systemId);
    boolean isInMaintenance(UUID systemId);

    // Security
    UniSystem updateSecurityConfig(UUID systemId, Map<String, Object> securityConfig);
    List<String> getSystemAllowedIps(UUID systemId);
    UniSystem updateAllowedIps(UUID systemId, Set<String> ipAddresses);
    UniSystem blockIp(UUID systemId, String ipAddress);
    UniSystem unblockIp(UUID systemId, String ipAddress);

    // Custom Integration
    Object callSystemApi(UUID systemId, String endpoint, String method, Map<String, Object> params, Object body);
    UniSystem testConnection(UUID systemId);
    UniSystem validateIntegration(UUID systemId);
}