package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private String systemCode;
    private String systemName;
    private Boolean isSystemRole;
    private LocalDateTime createdAt;
    private Set<SimplePermissionDTO> permissions;
    private Set<SimpleUserDTO> users;
}

