package ai.uniauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException() {
        super("Resource already exists");
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyExistsException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resourceType, identifier));
    }

    public ResourceAlreadyExistsException(String resourceType, String identifier, String value) {
        super(String.format("%s with %s '%s' already exists", resourceType, identifier, value));
    }

    // Factory methods for common cases
    public static ResourceAlreadyExistsException forUsername(String username) {
        return new ResourceAlreadyExistsException("User", "username", username);
    }

    public static ResourceAlreadyExistsException forEmail(String email) {
        return new ResourceAlreadyExistsException("User", "email", email);
    }

    public static ResourceAlreadyExistsException forSystemCode(String systemCode) {
        return new ResourceAlreadyExistsException("System", "code", systemCode);
    }

    public static ResourceAlreadyExistsException forRoleCode(String roleCode) {
        return new ResourceAlreadyExistsException("Role", "code", roleCode);
    }

    public static ResourceAlreadyExistsException forPermissionCode(String permissionCode) {
        return new ResourceAlreadyExistsException("Permission", "code", permissionCode);
    }

    public static ResourceAlreadyExistsException forApiKey(String apiKey) {
        return new ResourceAlreadyExistsException("System", "API key", apiKey.substring(0, Math.min(8, apiKey.length())) + "***");
    }
}