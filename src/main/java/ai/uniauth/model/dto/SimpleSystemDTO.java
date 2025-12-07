package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSystemDTO {
    private Long id;
    private String systemCode;
    private String systemName;
    private String baseUrl;
    private Boolean isActive;
}
