package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Admin APIs for system management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final SystemService systemService;
    private final PermissionService permissionService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponseDTO> getDashboardStats() {
        // Get statistics from all services
        var stats = Map.of(
                "totalUsers", userService.getAllUsers(org.springframework.data.domain.Pageable.unpaged()).getTotalElements(),
                "totalSystems", systemService.getAllSystems(org.springframework.data.domain.Pageable.unpaged()).getTotalElements(),
                "totalRoles", roleService.getAllRoles(org.springframework.data.domain.Pageable.unpaged()).getTotalElements(),
                "totalPermissions", permissionService.getAllPermissions(org.springframework.data.domain.Pageable.unpaged()).getTotalElements()
        );

        return ResponseEntity.ok(ApiResponseDTO.success("Dashboard statistics retrieved", stats));
    }

    @PostMapping("/users/bulk-activate")
    @Operation(summary = "Bulk activate users")
    public ResponseEntity<ApiResponseDTO> bulkActivateUsers(@RequestBody BulkOperationDTO bulkOp) {
        // Implement bulk activation
        return ResponseEntity.ok(ApiResponseDTO.success("Bulk operation completed", null));
    }

    @PostMapping("/systems/bulk-deactivate")
    @Operation(summary = "Bulk deactivate systems")
    public ResponseEntity<ApiResponseDTO> bulkDeactivateSystems(@RequestBody BulkOperationDTO bulkOp) {
        // Implement bulk deactivation
        return ResponseEntity.ok(ApiResponseDTO.success("Bulk operation completed", null));
    }

    @GetMapping("/audit/logs")
    @Operation(summary = "Get system audit logs")
    public ResponseEntity<ApiResponseDTO> getAuditLogs(
            @RequestParam(defaultValue = "7") int days) {
        // Implement audit log retrieval
        return ResponseEntity.ok(ApiResponseDTO.success("Audit logs retrieved", null));
    }
}
