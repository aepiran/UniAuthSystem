package ai.uniauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO {
    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    public static ApiResponseDTO success(String message, Object data) {
        return new ApiResponseDTO(true, message, data, LocalDateTime.now());
    }

    public static ApiResponseDTO error(String message) {
        return new ApiResponseDTO(false, message, null, LocalDateTime.now());
    }
}
