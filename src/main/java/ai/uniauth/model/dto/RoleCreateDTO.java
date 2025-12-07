package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateDTO {
    @NotBlank(message = "Role code is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role code must be uppercase with underscores")
    private String roleCode;

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;

    @NotBlank(message = "System code is required")
    private String systemCode;

    private Boolean isSystemRole = false;

    @NotNull(message = "Permission IDs are required")
    private Set<Long> permissionIds;
}
