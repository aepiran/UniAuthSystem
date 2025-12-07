package ai.uniauth.service.dto.request;

import lombok.Data;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String phoneNumber;
    private String department;
    private String position;
    private String avatarUrl;
    private String timezone;
    private String locale;
    private Map<String, Object> metadata;
    private Set<UUID> roleIds;
    private boolean forcePasswordChange;
}