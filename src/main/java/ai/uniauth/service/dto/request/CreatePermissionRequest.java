package ai.uniauth.service.dto.request;

import ai.uniauth.models.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePermissionRequest {
    @NotBlank
    @Pattern(regexp = "^[A-Z]+_[A-Z_]+$")
    private String code;

    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    private String description;

    @NotNull
    private UUID systemId;

    private String category;
    private String subcategory;
    private String module;
    private RiskLevel riskLevel = RiskLevel.LOW;
    private boolean isSensitive = false;
    private boolean requiresApproval = false;
}