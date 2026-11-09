package com.family.memoir.module.health;

import com.family.memoir.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Tag(name = "健康检查")
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${spring.application.name}")
    private String applicationName;

    @Operation(summary = "服务健康状态")
    @GetMapping
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of(
                "status", "UP",
                "application", applicationName,
                "time", LocalDateTime.now().format(FORMATTER)
        ));
    }
}
