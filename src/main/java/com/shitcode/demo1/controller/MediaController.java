package com.shitcode.demo1.controller;

import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.MediaService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@RestController
@LogCollector
@RequestMapping("/media")
@Tag(name = "Media Controller", description = "APIs for managing media")
public class MediaController {
    private final MediaService mediaService;
    private final ResponseService responseService;
    private final RateLimiterConfigData rateLimiterConfigData;

    public MediaController(MediaService mediaService, ResponseService responseService,
            RateLimiterConfigData rateLimiterConfigData) {
        this.mediaService = mediaService;
        this.responseService = responseService;
        this.rateLimiterConfigData = rateLimiterConfigData;
    }

    private RateLimiterPlan ID_PLAN;

    @PostConstruct
    public void setup() {
        ID_PLAN = rateLimiterConfigData.getRateLimiterPlan("media", "id");
    }

    @GetMapping("/{type}/{size}/{year}/{month}/{day}/{fileName:.+}")
    public ResponseEntity<Resource> findLocalMediaByPath(
            @PathVariable @Pattern(regexp = "^(images|videos)$", message = "Invalid media type") String type,
            @PathVariable @Pattern(regexp = "^(original|compressed)$", message = "Invalid size type") String size,
            @PathVariable @Min(2000) @Max(2100) Integer year,
            @PathVariable @Min(1) @Max(12) Integer month,
            @PathVariable @Min(1) @Max(31) Integer day,
            @PathVariable @NotBlank String fileName) throws Exception {
        String mediaPath = Paths.get(type, size,
                String.valueOf(year),
                String.valueOf(month),
                String.valueOf(day),
                fileName)
                .normalize().toString();

        Resource resource = mediaService.findFile("/".concat(mediaPath));

        // Determine content type
        String contentType = determineContentType(mediaPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String determineContentType(String mediaPath) {
        String extension = FilenameUtils.getExtension(mediaPath).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "mp4" -> "video/mp4";
            case "webm" -> "video/webm";
            default -> "application/octet-stream";
        };
    }
}
