package ai.uniauth.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private String fullName;
    private String phoneNumber;
    private String department;
    private String position;
    private Set<UUID> roleIds;
    private boolean sendWelcomeEmail = true;
}