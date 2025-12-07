package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemRegistrationResponseDTO {
    private String systemCode;
    private String systemName;
    private String apiKey;
    private String secretKey;
    private String message;
    private LocalDateTime registeredAt;
}
