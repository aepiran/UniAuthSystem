package ai.uniauth.service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class RoleDTO {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private UUID systemId;
    private String systemCode;
    private boolean isSystemRole;
    private boolean isDefault;
    private UUID parentRoleId;
    private Integer priority;
    private LocalDateTime createdAt;
    private Set<String> permissions;
    private long userCount;
}
