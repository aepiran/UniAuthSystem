package ai.uniauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Common business exceptions
    public static BusinessException userNotActive(String username) {
        return new BusinessException(
                String.format("User '%s' is not active", username),
                "USER_NOT_ACTIVE"
        );
    }

    public static BusinessException userLocked(String username) {
        return new BusinessException(
                String.format("User '%s' is locked", username),
                "USER_LOCKED"
        );
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid username or password",
                "INVALID_CREDENTIALS"
        );
    }

    public static BusinessException invalidSystemAccess() {
        return new BusinessException(
                "User does not have access to the requested system",
                "INVALID_SYSTEM_ACCESS"
        );
    }

    public static BusinessException operationNotAllowed() {
        return new BusinessException(
                "Operation not allowed",
                "OPERATION_NOT_ALLOWED"
        );
    }

    public static BusinessException systemInactive(String systemCode) {
        return new BusinessException(
                String.format("System '%s' is inactive", systemCode),
                "SYSTEM_INACTIVE"
        );
    }

    public static BusinessException cannotDeleteSystemWithUsers(Long userCount) {
        return new BusinessException(
                String.format("Cannot delete system with %d active users", userCount),
                "SYSTEM_HAS_USERS"
        );
    }

    public static BusinessException cannotDeleteRoleWithUsers() {
        return new BusinessException(
                "Cannot delete role that has users assigned",
                "ROLE_HAS_USERS"
        );
    }

    public static BusinessException cannotChangeSystemRole() {
        return new BusinessException(
                "Cannot change system role flag for existing system role",
                "CANNOT_CHANGE_SYSTEM_ROLE"
        );
    }

    public static BusinessException permissionNotInSystem() {
        return new BusinessException(
                "Permission does not belong to the system",
                "PERMISSION_NOT_IN_SYSTEM"
        );
    }

    public static BusinessException roleNotInSystem() {
        return new BusinessException(
                "Role does not belong to user's system",
                "ROLE_NOT_IN_SYSTEM"
        );
    }
}