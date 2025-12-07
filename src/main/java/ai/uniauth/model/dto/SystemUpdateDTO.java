package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpdateDTO {
    private String systemName;
    private String description;
    private String baseUrl;
    private String contactEmail;
    private Boolean isActive;
}
