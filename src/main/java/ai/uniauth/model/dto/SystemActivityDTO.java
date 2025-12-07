package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemActivityDTO {
    private LocalDateTime timestamp;
    private String activityType;
    private String description;
    private String username;
    private String ipAddress;
}
