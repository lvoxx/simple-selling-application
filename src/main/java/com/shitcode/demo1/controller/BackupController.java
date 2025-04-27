package com.shitcode.demo1.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.BackupService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@RestController
@LogCollector
@RequestMapping("/backup")
@Tag(name = "Backup Controller", description = "APIs for backing up data")
public class BackupController {
    private final BackupService backupService;
    private final ResponseService responseService;
    private final RateLimiterConfigData rateLimiterConfigData;

    public BackupController(BackupService backupService, ResponseService responseService,
            RateLimiterConfigData rateLimiterConfigData) {
        this.backupService = backupService;
        this.responseService = responseService;
        this.rateLimiterConfigData = rateLimiterConfigData;
    }

    private RateLimiterPlan MEDIA_BACKUP_PLAN;
    private RateLimiterPlan SQL_BACKUP_PLAN;

    @PostConstruct
    public void setup() {
        MEDIA_BACKUP_PLAN = rateLimiterConfigData.getRateLimiterPlan("backup", "media");
        SQL_BACKUP_PLAN = rateLimiterConfigData.getRateLimiterPlan("backup", "sql");
    }

    @Operation(summary = "Backup media files to cloud storage", description = "Backs up either image or video files from a specific date to cloud storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media files backed up successfully", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error during backup", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(name = "/media", produces = "application/vnd.lvoxx.app-v1+json")
    public ResponseEntity<?> backupMedia(
            @Parameter(description = "Date for which to backup files (defaults to current date)") @RequestParam(name = "d", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date,
            @Parameter(description = "Type of media to backup (image/video)") @Pattern(regexp = "^(image|video)$", message = "Type must be either 'image' or 'video'") @RequestParam(name = "t", required = false, defaultValue = "image") String type)
            throws Exception {
        return responseService.mapping(
                () -> {
                    LocalDate time = Optional.ofNullable(date).orElse(LocalDate.now());
                    List<String> result = type.equals("image")
                            ? backupService.backupImageFolderToCloud(time)
                            : backupService.backupVideoFolderToCloud(time);
                    return ResponseEntity.ok().body(result);
                },
                MEDIA_BACKUP_PLAN);
    }
}
