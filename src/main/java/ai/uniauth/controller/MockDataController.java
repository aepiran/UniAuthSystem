package ai.uniauth.controller;

import ai.uniauth.service.MockDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mock-data")
@RequiredArgsConstructor
@Tag(name = "Mock Data Management", description = "APIs for generating and managing mock/test data")
public class MockDataController {

    private final MockDataService mockDataService;

    @PostMapping("/generate")
    @Operation(summary = "Generate basic test data with demo system")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateTestData() {
        Map<String, Object> result = mockDataService.generateTestData();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/generate-system")
    @Operation(summary = "Generate a new system with sample data")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateSystem(
            @RequestParam String systemCode,
            @RequestParam String systemName) {
        Map<String, Object> result = mockDataService.generateSystemWithData(systemCode, systemName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/generate-large")
    @Operation(summary = "Generate large dataset for performance testing")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateLargeDataSet(
            @RequestParam(defaultValue = "1000") int users,
            @RequestParam(defaultValue = "50") int roles,
            @RequestParam(defaultValue = "200") int permissions) {

        if (users > 10000 || roles > 200 || permissions > 1000) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Values too large. Max: users=10000, roles=200, permissions=1000"
            ));
        }

        Map<String, Object> result = mockDataService.generateLargeDataSet(users, roles, permissions);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "Clean up all test data (TEST_, LARGE_, DEMO systems)")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupTestData() {
        Map<String, Object> result = mockDataService.cleanupTestData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get current data statistics")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMockDataStatistics() {
        Map<String, Object> stats = mockDataService.getMockDataStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/credentials")
    @Operation(summary = "Get sample user credentials for testing")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getSampleCredentials() {
        Map<String, String> credentials = mockDataService.getSampleCredentials();
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/reset-demo")
    @Operation(summary = "Reset demo data to initial state")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetDemoData() {
        Map<String, Object> result = mockDataService.resetDemoData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/system/{systemCode}/summary")
    @Operation(summary = "Get summary of a specific system")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemSummary(
            @Parameter(description = "System code") @PathVariable String systemCode) {
        Map<String, Object> summary = mockDataService.getSystemSummary(systemCode);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/health")
    @Operation(summary = "Check mock data service health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "MockDataService",
                "timestamp", java.time.LocalDateTime.now(),
                "availableMethods", Arrays.asList(
                        "generate", "generate-system", "generate-large",
                        "cleanup", "statistics", "credentials", "reset-demo"
                )
        );

        return ResponseEntity.ok(health);
    }
}