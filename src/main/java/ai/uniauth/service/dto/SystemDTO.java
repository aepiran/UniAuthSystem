package ai.uniauth.service.dto;

import ai.uniauth.models.enums.AuthType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class SystemDTO {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String baseUrl;
    private String apiEndpoint;
    private String webhookUrl;
    private AuthType authType;
    private boolean isActive;
    private boolean isInternal;
    private LocalDateTime registeredAt;
    private LocalDateTime lastSyncAt;
    private LocalDateTime lastHealthCheck;
    private Map<String, Object> config;
    private Integer rateLimit;
    private Integer timeoutMs;
    private long userCount;
    private long roleCount;
    private long permissionCount;
    private boolean isHealthy;
}