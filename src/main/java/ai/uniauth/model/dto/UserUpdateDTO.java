package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String phone;
    private String fullName;
    private String avatarUrl;
    private Boolean isActive;
    private Boolean isLocked;
}
