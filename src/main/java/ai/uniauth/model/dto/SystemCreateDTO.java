package ai.uniauth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemCreateDTO {
    @NotBlank(message = "System code is required")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]{2,49}$",
            message = "System code must start with uppercase letter, contain only letters, numbers and underscores, 3-50 characters")
    private String systemCode;

    @NotBlank(message = "System name is required")
    @Size(min = 2, max = 100, message = "System name must be between 2 and 100 characters")
    private String systemName;

    private String description;

    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Base URL must start with http:// or https://")
    private String baseUrl;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    private String contactEmail;

    private Boolean isActive = true;
}
