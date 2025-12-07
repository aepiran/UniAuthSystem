package ai.uniauth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "UniAuth System", description = "UniAuth")
public class UniAuthController {

    @Value("${uni.build.version:unknown}")
    private String buildVersion;

    @Value("${uni.build.timestamp:unknown}")
    private String buildTimestamp;

    @GetMapping("/api")
    public Map<String, String> getBuildInfo() {
        return Map.of(
                "appName", "UniAuth System",
                "version", getVersion(),
                "buildVersion", buildVersion,
                "buildTime", buildTimestamp,
                "javaVersion", Runtime.version().toString()
        );
    }
    private String getVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            return "unknown";
        }
        return version;
    }
}
