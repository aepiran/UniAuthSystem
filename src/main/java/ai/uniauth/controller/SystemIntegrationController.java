package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.SystemService;
import ai.uniauth.service.UserService;
import ai.uniauth.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/integration")
@RequiredArgsConstructor
@Tag(name = "System Integration", description = "APIs for external system integration")
public class SystemIntegrationController {

    private final SystemService systemService;
    private final UserService userService;
    private final PermissionService permissionService;

    @PostMapping("/validate-access")
    @Operation(summary = "Validate system access with API key")
    public ResponseEntity<ApiResponseDTO> validateSystemAccess(
            @RequestHeader("X-System-Code") String systemCode,
            @RequestHeader("X-API-Key") String apiKey) {

        boolean isValid = systemService.validateSystemAccess(systemCode, apiKey);
        if (!isValid) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        log.info("System access validated for: {}", systemCode);
        return ResponseEntity.ok(ApiResponseDTO.success("Access validated", null));
    }

    @GetMapping("/permissions/check")
    @Operation(summary = "Check user permission (for external systems)")
    public ResponseEntity<ApiResponseDTO> checkUserPermission(
            @RequestHeader("X-System-Code") String systemCode,
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam String username,
            @RequestParam String permissionCode) {

        // Validate system access first
        if (!systemService.validateSystemAccess(systemCode, apiKey)) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        boolean hasPermission = permissionService.hasPermission(username, permissionCode);
        log.debug("Permission check for user {}: {} = {}", username, permissionCode, hasPermission);

        return ResponseEntity.ok(ApiResponseDTO.success(
                "Permission check completed",
                Map.of("hasPermission", hasPermission)
        ));
    }

    @GetMapping("/user/{username}/permissions")
    @Operation(summary = "Get user permissions (for external systems)")
    public ResponseEntity<ApiResponseDTO> getUserPermissions(
            @RequestHeader("X-System-Code") String systemCode,
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String username) {

        if (!systemService.validateSystemAccess(systemCode, apiKey)) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        Set<String> permissions = permissionService.getPermissionCodesByUserAndSystem(
                userService.getUserByUsername(username).getId(),
                systemCode
        );

        log.debug("Retrieved {} permissions for user {}", permissions.size(), username);
        return ResponseEntity.ok(ApiResponseDTO.success("User permissions retrieved", permissions));
    }

    @PostMapping("/user/verify")
    @Operation(summary = "Verify user credentials (for external systems)")
    public ResponseEntity<ApiResponseDTO> verifyUser(
            @RequestHeader("X-System-Code") String systemCode,
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody UserVerificationDTO verificationDTO) {

        if (!systemService.validateSystemAccess(systemCode, apiKey)) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        UserDTO user = userService.verifyUser(verificationDTO);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid credentials"));
        }

        // Check if user belongs to the requesting system
        if (!user.getAssignedSystemCodes().contains(systemCode)) {
            return ResponseEntity.status(403).body(ApiResponseDTO.error("User not authorized for this system"));
        }

        // Get user's permissions for this system
        Set<String> permissions = permissionService.getPermissionCodesByUserAndSystem(user.getId(), systemCode);
        Set<String> roles = user.getRoles().stream()
                .map(SimpleRoleDTO::getRoleCode)
                .collect(java.util.stream.Collectors.toSet());

        var response = Map.of(
                "user", user,
                "permissions", permissions,
                "roles", roles
        );

        log.info("User verified: {}", user.getUsername());
        return ResponseEntity.ok(ApiResponseDTO.success("User verified successfully", response));
    }

    @GetMapping("/system/{systemCode}/info")
    @Operation(summary = "Get system information")
    public ResponseEntity<ApiResponseDTO> getSystemInfo(
            @RequestHeader("X-System-Code") String requestingSystemCode,
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String systemCode) {

        if (!systemService.validateSystemAccess(requestingSystemCode, apiKey)) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        UniSystemDTO system = systemService.getSystemByCode(systemCode);
        return ResponseEntity.ok(ApiResponseDTO.success("System information retrieved", system));
    }

    @PostMapping("/user/sync")
    @Operation(summary = "Sync user data from external system")
    public ResponseEntity<ApiResponseDTO> syncUserData(
            @RequestHeader("X-System-Code") String systemCode,
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody UserSyncDTO userSyncDTO) {

        if (!systemService.validateSystemAccess(systemCode, apiKey)) {
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid system access"));
        }

        // Implement user synchronization logic here
        // This could create/update users from external system
        log.info("User sync requested for system: {}", systemCode);

        return ResponseEntity.ok(ApiResponseDTO.success("User sync initiated", null));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<ApiResponseDTO> healthCheck() {
        var healthInfo = Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now(),
                "version", "1.0.0"
        );

        return ResponseEntity.ok(ApiResponseDTO.success("UniAuth System is running", healthInfo));
    }
}



