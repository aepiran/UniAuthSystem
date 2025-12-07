package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatusDTO {
    private String systemCode;
    private String systemName;
    private Boolean isActive;
    private LocalDateTime lastActive;
    private Integer activeUsers;
    private Integer totalRequests;
}
