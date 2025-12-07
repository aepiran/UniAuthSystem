package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionAssignmentDTO {
    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "Permission IDs are required")
    private Set<Long> permissionIds;
}
