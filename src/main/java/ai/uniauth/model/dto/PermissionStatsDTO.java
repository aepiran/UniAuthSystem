package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionStatsDTO {
    private String systemCode;
    private Long totalPermissions;
    private Long activePermissions;
    private Map<String, Long> permissionsByResourceType;
    private Map<String, Long> permissionsByAction;
    private Long permissionsAssignedToRoles;
    private Long uniquePermissionsAssignedToUsers;
    private LocalDateTime lastPermissionCreated;
}