package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRoleDTO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String systemCode;
}
