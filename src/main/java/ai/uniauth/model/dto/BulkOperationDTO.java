package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationDTO {
    private java.util.Set<Long> ids;
    private String operation;
    private String reason;
}