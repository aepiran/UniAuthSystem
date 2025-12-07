package ai.uniauth.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiResponse {
    private boolean success;
    private String code;
    private String message;
    private String detail;
    private Instant timestamp;
    private Object data;
}