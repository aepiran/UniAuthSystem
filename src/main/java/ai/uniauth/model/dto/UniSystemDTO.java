package ai.uniauth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UniSystemDTO {
    private Long id;
    private String systemCode;
    private String systemName;
    private String description;
    private String baseUrl;
    private String contactEmail;
    private Boolean isActive;
    private String apiKey; // Masked in response
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<SimpleRoleDTO> roles;
    private Set<SimplePermissionDTO> permissions;
    private Set<SimpleUserDTO> users;
    private Integer userCount;
    private Integer roleCount;
    private Integer permissionCount;
}
