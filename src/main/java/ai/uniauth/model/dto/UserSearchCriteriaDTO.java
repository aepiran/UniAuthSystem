package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteriaDTO {
    private String keyword;
    private Set<String> systemCodes;
    private Set<String> roleCodes;
    private Boolean isActive;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
}
