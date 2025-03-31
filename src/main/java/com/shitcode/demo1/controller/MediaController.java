package com.shitcode.demo1.controller;

import org.springframework.core.io.Resource;
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

    @GetMapping
    public ResponseEntity<Resource> findMediaByPath(@PathVariable String mediaPath) throws Exception {
        return ResponseEntity.ok().body(mediaService.findFile(mediaPath));
    }

}
