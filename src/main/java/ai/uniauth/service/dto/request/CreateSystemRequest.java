package ai.uniauth.service.dto.request;
import ai.uniauth.models.enums.AuthType;
import ai.uniauth.models.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateSystemRequest {
    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$")
    private String code;

    private String description;
    private String baseUrl;
    private String apiEndpoint;
    private String webhookUrl;
    private AuthType authType = AuthType.JWT;
    private boolean isInternal = false;
    private Map<String, Object> config;
    private Integer rateLimit = 1000;
    private Integer timeoutMs = 5000;
}