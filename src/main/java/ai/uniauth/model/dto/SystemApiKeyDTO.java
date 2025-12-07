package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemApiKeyDTO {
    @NotBlank(message = "System code is required")
    private String systemCode;

    @NotBlank(message = "Current API key is required")
    private String currentApiKey;
}
