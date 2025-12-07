package ai.uniauth.service.dto;

import ai.uniauth.models.enums.RiskLevel;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PermissionDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private UUID systemId;
    private String systemCode;
    private String category;
    private String subcategory;
    private String module;
    private RiskLevel riskLevel;
    private boolean isSensitive;
    private boolean requiresApproval;
    private LocalDateTime createdAt;
    private long roleCount;
}