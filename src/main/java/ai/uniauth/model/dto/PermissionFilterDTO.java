package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionFilterDTO {
    private String systemCode;
    private String resourceType;
    private String action;
    private String keyword;
    private Boolean assignedToRole;
    private Long roleId;
    private Boolean assignedToUser;
    private Long userId;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
}