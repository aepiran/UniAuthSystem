package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSyncRequestDTO {
    @NotBlank(message = "System code is required")
    private String systemCode;

    @NotBlank(message = "API key is required")
    private String apiKey;

    private String syncType; // USERS, ROLES, PERMISSIONS, ALL
    private LocalDateTime since;
}
