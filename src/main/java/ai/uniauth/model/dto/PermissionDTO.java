package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String resourceType;
    private String action;
    private String systemCode;
    private LocalDateTime createdAt;
}

