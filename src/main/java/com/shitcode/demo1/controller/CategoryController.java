package com.shitcode.demo1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@RestController
@LogCollector
@RequestMapping(path = "/categories", produces = "application/vnd.lvoxx.app-v1+json")
@Tag(name = "Category Controller", description = "APIs for managing categories")
public class CategoryController {

        private final CategoryService categoryService;
        private final ResponseService responseService;
        private final RateLimiterConfigData rateLimiterConfigData;

        public CategoryController(CategoryService categoryService, ResponseService responseService,
                        RateLimiterConfigData rateLimiterConfigData) {
                this.categoryService = categoryService;
                this.responseService = responseService;
                this.rateLimiterConfigData = rateLimiterConfigData;
        }

        private RateLimiterPlan ID_PLAN;
        private RateLimiterPlan FIND_ALL_PLAN;
        private RateLimiterPlan MANAGE_PLAN;

        @PostConstruct
        public void setup() {
                ID_PLAN = rateLimiterConfigData.getRateLimiterPlan("category", "id");
                FIND_ALL_PLAN = rateLimiterConfigData.getRateLimiterPlan("category", "find-all");
                MANAGE_PLAN = rateLimiterConfigData.getRateLimiterPlan("category", "manage");
        }

        @GetMapping
        @Operation(summary = "Get all categories with pagination", description = "Retrieves a paginated list of categories. Supports sorting and ordering.", responses = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<?> findAllWithPaginationV1(
                        @Parameter(description = "Number of categories per page", example = "30") @RequestParam(name = "s", defaultValue = "30") int size,

                        @Parameter(description = "Page number to retrieve", example = "1") @RequestParam(name = "p", defaultValue = "1") int page,

                        @Parameter(description = "Field to sort by", example = "id") @RequestParam(name = "sb", defaultValue = "id") String sort,

                        @Parameter(description = "Sort order, true for ascending", example = "false") @RequestParam(name = "a", defaultValue = "false") boolean asc) {
                return responseService.mapping(
                                () -> ResponseEntity.ok().body(
                                                categoryService.findCategoryWithPagination(page, size, sort, asc)),
                                FIND_ALL_PLAN);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get category by ID", description = "Retrieves details of a category by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the category"),
                        @ApiResponse(responseCode = "404", description = "Category not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        public ResponseEntity<?> findByIdV1(
                        @Parameter(description = "ID of the category to retrieve", required = true) @PathVariable Long id) {
                return responseService.mapping(() -> ResponseEntity.ok().body(categoryService.findCategoryById(id)),
                                ID_PLAN);
        }

        @PostMapping
        @Operation(summary = "Create a new category", description = "Adds a new category to the system. Requires a valid JWT token for authentication.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),

                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> createCategoryByBodyV1(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category details to be created", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.Request.class))) @Valid @RequestBody CategoryDTO.Request request) {

                return responseService.mapping(
                                () -> new ResponseEntity<>(categoryService.createCategory(request), HttpStatus.CREATED),
                                MANAGE_PLAN);

        }

        @PutMapping("/{id}")
        @Operation(summary = "Update an existing category", description = "Updates the details of an existing category by its ID. Requires a valid JWT token for authentication.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),

                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> updateCategoryByIdAndBodyV1(
                        @Parameter(description = "ID of the category to update", required = true) @PathVariable Long id,
                        @Valid @RequestBody CategoryDTO.Request request) {

                return responseService.mapping(
                                () -> ResponseEntity.ok(categoryService.updateCategory(request, id)),
                                MANAGE_PLAN);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a category", description = "Deletes a category by its ID. Requires a valid JWT token for authentication.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),

                        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),

                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class)))
        })
        public ResponseEntity<?> deleteCategoryByIdV1(
                        @Parameter(description = "ID of the category to delete", required = true) @PathVariable Long id) {

                return responseService.mapping(
                                () -> {
                                        categoryService.deleteCategoryById(id);
                                        // * Returns 204 No Content for successful deletion
                                        return ResponseEntity.noContent().build();
                                },
                                MANAGE_PLAN);
        }

}
