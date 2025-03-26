package com.shitcode.demo1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@RestController
@LogCollector
@RequestMapping(path = "/product-interaction", produces = "application/vnd.lvoxx.app-v1+json")
@Tag(name = "Product Interaction Controller", description = "APIs for requesting product interations history")
public class ProductInteractionController {
    private final InterationEventService interationEventService;
    private final ResponseService responseService;
    private final RateLimiterConfigData rateLimiterConfigData;

    public ProductInteractionController(InterationEventService interationEventService,
            RateLimiterConfigData rateLimiterConfigData, ResponseService responseService) {
        this.interationEventService = interationEventService;
        this.responseService = responseService;
        this.rateLimiterConfigData = rateLimiterConfigData;
    }

    private RateLimiterPlan PRODUCT_INTERACTION_PLAN;

    @PostConstruct
    public void setup() {
        PRODUCT_INTERACTION_PLAN = rateLimiterConfigData.getRateLimiterPlan("product-interaction", "time");
    }

    @GetMapping("/time-between")
    @Operation(summary = "Retrieve product interaction with paging", description = "Fetches a paginated list of in-sell products, allowing sorting and ordering.", responses = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of product interaction", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
    })
    public ResponseEntity<?> getInteractionEventByTimeBetween(
            @RequestParam(name = "s", defaultValue = "10000") Integer size,
            @RequestParam(name = "p", defaultValue = "0") Integer page,
            @RequestParam(name = "st", required = true) LocalDateTime startTime,
            @RequestParam(name = "et", required = true) LocalDateTime endTime)
            throws Exception {
                // 5 minutes: size 10k x 20 (reqs / m) = 200k rows
                // A hour: 200k x 12 = 2.4 mil rows
        return responseService.mapping(
                () -> ResponseEntity.ok().body(
                        interationEventService.findRecordsWithTimeBetween(page, size, startTime, endTime)),
                PRODUCT_INTERACTION_PLAN);
    }

}
