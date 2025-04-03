package com.shitcode.demo1.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.service.ProductService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@RestController
@LogCollector
@RequestMapping(path = "/products", produces = "application/vnd.lvoxx.app-v1+json")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {
        private final ProductService productService;
        private final ResponseService responseService;

        public ProductController(ProductService productService, ResponseService responseService) {
                this.productService = productService;
                this.responseService = responseService;
        }

        @GetMapping("/insell")
        @Operation(summary = "Retrieve in-sell products with pagination", description = "Fetches a paginated list of in-sell products, allowing sorting and ordering.", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful retrieval of in-sell products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> getInSellProducts(
                        @Parameter(description = "Number of insell products per page", example = "30") @RequestParam(defaultValue = "30") int size,

                        @Parameter(description = "Page number to retrieve", example = "1") @RequestParam(defaultValue = "1") int page,

                        @Parameter(description = "Field to sort by", example = "id") @RequestParam(defaultValue = "id") String sort,

                        @Parameter(description = "Sort order, true for ascending", example = "false") @RequestParam(defaultValue = "false") boolean asc)
                        throws Exception {
                return responseService.mapping(
                                () -> ResponseEntity.ok()
                                                .body(productService.findInSellWithPagination(page, size, sort, asc)),
                                RateLimiterPlan.SOFT);
        }

        @GetMapping("/admin")
        @Operation(summary = "Retrieve admin products with pagination", description = "Fetches a paginated list of admin products, allowing sorting and ordering.", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful retrieval of admin products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> getAdminProducts(
                        @Parameter(description = "Number of insell products per page", example = "30") @RequestParam(defaultValue = "30") int size,

                        @Parameter(description = "Page number to retrieve", example = "1") @RequestParam(defaultValue = "1") int page,

                        @Parameter(description = "Field to sort by", example = "id") @RequestParam(defaultValue = "id") String sort,

                        @Parameter(description = "Sort order, true for ascending", example = "false") @RequestParam(defaultValue = "false") boolean asc)
                        throws Exception {
                return responseService.mapping(
                                () -> ResponseEntity.ok()
                                                .body(productService.findAdminWithPagination(page, size, sort, asc)),
                                RateLimiterPlan.BASIC);
        }

        @Operation(summary = "Find an admin product by ID", description = "Retrieves detailed information of an admin product by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid product ID supplied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        @GetMapping("/admin/{id}")
        public ResponseEntity<?> findAdminProductById(
                        @Parameter(description = "ID of the admin product", example = "1") @PathVariable Long id)
                        throws Exception {
                return responseService.mapping(
                                () -> ResponseEntity.ok().body(productService.findAdminProductWithId(id)),
                                RateLimiterPlan.SOFT);
        }

        @Operation(summary = "Find an in-sell product by ID", description = "Retrieves detailed information of an in-sell product by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid product ID supplied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        @GetMapping("/insell/{id}")
        public ResponseEntity<?> findInSellProductById(
                        @Parameter(description = "ID of the in-sell product", example = "1") @PathVariable Long id)
                        throws Exception {
                return responseService.mapping(
                                () -> ResponseEntity.ok().body(productService.findInSellProductWithId(id)),
                                RateLimiterPlan.HEAVY_LOADS);
        }

        @PostMapping("/admin")
        @Operation(summary = "Create a new product", description = "Creates a new product with the provided details.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> createProduct(@RequestPart("json") @Valid ProductDTO.Request jsonRequest,
                        @RequestPart("images") List<MultipartFile> imageRequests,
                        @RequestPart("video") MultipartFile videoRequest) throws Exception {
                return responseService.mapping(
                                () -> new ResponseEntity<>(
                                                productService.create(jsonRequest, imageRequests, videoRequest),
                                                HttpStatus.CREATED),
                                RateLimiterPlan.BASIC);
        }
}
