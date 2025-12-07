package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemApiKeyResponseDTO {
    private String systemCode;
    private String newApiKey;
    private String newSecretKey;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
}
