package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role IDs are required")
    private Set<Long> roleIds;
}
