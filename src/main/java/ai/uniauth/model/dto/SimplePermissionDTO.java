package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePermissionDTO {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String action;
}
