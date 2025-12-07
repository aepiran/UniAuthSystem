package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateDTO {
    @NotBlank(message = "Permission code is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Permission code must be uppercase with underscores")
    private String permissionCode;

    @NotBlank(message = "Permission name is required")
    private String permissionName;

    private String description;

    @NotBlank(message = "Resource type is required")
    private String resourceType;

    @NotBlank(message = "Action is required")
    private String action;

    @NotBlank(message = "System code is required")
    private String systemCode;
}
