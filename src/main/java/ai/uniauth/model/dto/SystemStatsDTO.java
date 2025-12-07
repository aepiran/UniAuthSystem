package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsDTO {
    private String systemCode;
    private String systemName;
    private Long totalUsers;
    private Long activeUsers;
    private Long totalRoles;
    private Long totalPermissions;
    private Long dailyActiveUsers;
    private Long weeklyRequests;
    private Double averageResponseTime;
    private LocalDateTime lastSyncTime;
}
