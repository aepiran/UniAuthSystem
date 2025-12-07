package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
