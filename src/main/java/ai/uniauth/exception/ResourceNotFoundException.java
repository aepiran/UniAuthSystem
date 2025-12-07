package ai.uniauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s with ID %d not found", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier));
    }

    public ResourceNotFoundException(String resourceType, String identifier, String value) {
        super(String.format("%s with %s '%s' not found", resourceType, identifier, value));
    }

    // Factory methods for common cases
    public static ResourceNotFoundException forUser(Long userId) {
        return new ResourceNotFoundException("User", userId);
    }

    public static ResourceNotFoundException forUser(String username) {
        return new ResourceNotFoundException("User", "username", username);
    }

    public static ResourceNotFoundException forSystem(Long systemId) {
        return new ResourceNotFoundException("System", systemId);
    }

    public static ResourceNotFoundException forSystem(String systemCode) {
        return new ResourceNotFoundException("System", "code", systemCode);
    }

    public static ResourceNotFoundException forRole(Long roleId) {
        return new ResourceNotFoundException("Role", roleId);
    }

    public static ResourceNotFoundException forRole(String roleCode) {
        return new ResourceNotFoundException("Role", "code", roleCode);
    }

    public static ResourceNotFoundException forPermission(Long permissionId) {
        return new ResourceNotFoundException("Permission", permissionId);
    }

    public static ResourceNotFoundException forPermission(String permissionCode) {
        return new ResourceNotFoundException("Permission", "code", permissionCode);
    }
}