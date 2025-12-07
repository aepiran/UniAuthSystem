package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/systems")
@RequiredArgsConstructor
@Tag(name = "System Management", description = "APIs for managing UniAuth systems")
public class SystemController {

    private final SystemService systemService;

    @PostMapping("/register")
    @Operation(summary = "Register a new system")
    @PreAuthorize("hasPermission('SYSTEM_CREATE')")
    public ResponseEntity<SystemRegistrationResponseDTO> registerSystem(
            @Valid @RequestBody SystemCreateDTO systemCreateDTO) {
        SystemRegistrationResponseDTO response = systemService.registerSystem(systemCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get system by ID")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<UniSystemDTO> getSystemById(
            @Parameter(description = "System ID") @PathVariable Long id) {
        UniSystemDTO system = systemService.getSystemById(id);
        return ResponseEntity.ok(system);
    }

    @GetMapping("/code/{systemCode}")
    @Operation(summary = "Get system by code")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<UniSystemDTO> getSystemByCode(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        UniSystemDTO system = systemService.getSystemByCode(systemCode);
        return ResponseEntity.ok(system);
    }

    @GetMapping
    @Operation(summary = "Get all systems with pagination")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<Page<UniSystemDTO>> getAllSystems(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<UniSystemDTO> systems = systemService.getAllSystems(pageable);
        return ResponseEntity.ok(systems);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active systems")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<Page<UniSystemDTO>> getActiveSystems(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UniSystemDTO> systems = systemService.getActiveSystems(pageable);
        return ResponseEntity.ok(systems);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update system")
    @PreAuthorize("hasPermission('SYSTEM_UPDATE')")
    public ResponseEntity<UniSystemDTO> updateSystem(
            @Parameter(description = "System ID") @PathVariable Long id,
            @Valid @RequestBody SystemUpdateDTO systemUpdateDTO) {
        UniSystemDTO updatedSystem = systemService.updateSystem(id, systemUpdateDTO);
        return ResponseEntity.ok(updatedSystem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete system")
    @PreAuthorize("hasPermission('SYSTEM_DELETE')")
    public ResponseEntity<Void> deleteSystem(
            @Parameter(description = "System ID") @PathVariable Long id) {
        systemService.deleteSystem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate system")
    @PreAuthorize("hasPermission('SYSTEM_UPDATE')")
    public ResponseEntity<Void> activateSystem(
            @Parameter(description = "System ID") @PathVariable Long id) {
        systemService.activateSystem(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate system")
    @PreAuthorize("hasPermission('SYSTEM_UPDATE')")
    public ResponseEntity<Void> deactivateSystem(
            @Parameter(description = "System ID") @PathVariable Long id) {
        systemService.deactivateSystem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/regenerate-keys")
    @Operation(summary = "Regenerate API keys for system")
    @PreAuthorize("hasPermission('SYSTEM_UPDATE')")
    public ResponseEntity<SystemApiKeyResponseDTO> regenerateApiKeys(
            @Parameter(description = "System ID") @PathVariable Long id) {
        SystemApiKeyResponseDTO response = systemService.regenerateApiKeys(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/code/{systemCode}/regenerate-keys")
    @Operation(summary = "Regenerate API keys by system code")
    @PreAuthorize("hasPermission('SYSTEM_UPDATE')")
    public ResponseEntity<SystemApiKeyResponseDTO> regenerateApiKeysByCode(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        SystemApiKeyResponseDTO response = systemService.regenerateApiKeysByCode(systemCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate system API key")
    public ResponseEntity<Boolean> validateApiKey(
            @Valid @RequestBody SystemApiKeyDTO apiKeyDTO) {
        boolean isValid = systemService.validateApiKey(apiKeyDTO.getSystemCode(), apiKeyDTO.getCurrentApiKey());
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{systemCode}/status")
    @Operation(summary = "Get system status")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<SystemStatusDTO> getSystemStatus(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        SystemStatusDTO status = systemService.getSystemStatus(systemCode);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/all/status")
    @Operation(summary = "Get status of all systems")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<List<SystemStatusDTO>> getAllSystemsStatus() {
        List<SystemStatusDTO> statusList = systemService.getAllSystemsStatus();
        return ResponseEntity.ok(statusList);
    }

    @GetMapping("/{id}/users")
    @Operation(summary = "Get users of a system")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<Page<UserDTO>> getSystemUsers(
            @Parameter(description = "System ID") @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserDTO> users = systemService.getSystemUsers(id, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get roles of a system")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<Page<RoleDTO>> getSystemRoles(
            @Parameter(description = "System ID") @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RoleDTO> roles = systemService.getSystemRoles(id, pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "Get permissions of a system")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<Page<PermissionDTO>> getSystemPermissions(
            @Parameter(description = "System ID") @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PermissionDTO> permissions = systemService.getSystemPermissions(id, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{systemCode}/statistics")
    @Operation(summary = "Get system statistics")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<SystemStatsDTO> getSystemStatistics(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        SystemStatsDTO stats = systemService.getSystemStatistics(systemCode);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{systemCode}/activity")
    @Operation(summary = "Get system activity logs")
    @PreAuthorize("hasPermission('SYSTEM_READ')")
    public ResponseEntity<List<SystemActivityDTO>> getSystemActivity(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @RequestParam(defaultValue = "7") int days) {
        List<SystemActivityDTO> activity = systemService.getSystemActivity(systemCode, days);
        return ResponseEntity.ok(activity);
    }
}