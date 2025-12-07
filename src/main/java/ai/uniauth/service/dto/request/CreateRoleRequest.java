package ai.uniauth.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class CreateRoleRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^ROLE_[A-Z_]+$")
    private String code;

    private String description;
    private UUID systemId;
    private UUID parentRoleId;
    private boolean isDefault = false;
    private Integer priority = 0;
    private Set<UUID> permissionIds;
}