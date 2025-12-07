package ai.uniauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check and monitoring APIs")
public class HealthController {

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    @Operation(summary = "Application health check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> healthInfo = Map.of(
                "status", "UP",
                "service", "UniAuth System",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now(),
                "database", "CONNECTED", // You would check DB connection here
                "cache", "CONNECTED"     // You would check cache connection here
        );

        return ResponseEntity.ok(healthInfo);
    }

    @GetMapping("/ready")
    @Operation(summary = "Application readiness probe")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> readinessInfo = Map.of(
                "status", "READY",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(readinessInfo);
    }

    @GetMapping("/live")
    @Operation(summary = "Application liveness probe")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> livenessInfo = Map.of(
                "status", "ALIVE",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(livenessInfo);
    }

    @PostMapping("/state/ready/{state}")
    @Operation(summary = "Change application readiness state (admin only)")
    public ResponseEntity<Void> changeReadinessState(@PathVariable String state) {
        if ("accepting".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
        } else if ("refusing".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/state/live/{state}")
    @Operation(summary = "Change application liveness state (admin only)")
    public ResponseEntity<Void> changeLivenessState(@PathVariable String state) {
        if ("correct".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, LivenessState.CORRECT);
        } else if ("broken".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, LivenessState.BROKEN);
        }
        return ResponseEntity.ok().build();
    }
}