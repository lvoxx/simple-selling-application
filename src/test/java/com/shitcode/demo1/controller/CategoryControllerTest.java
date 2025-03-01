package com.shitcode.demo1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

@SuppressWarnings("unchecked")
@WebMvcTest(CategoryController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({
        @Tag("Controller"), @Tag("Mock")
})
@DisplayName("Category controller tests with mocking")
// Import needed components
@Import({ RateLimiterConfigData.class })
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CategoryService categoryService;

    @MockitoBean
    ResponseService responseService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should return paging when requesting GET with valid request parameters on findAllWithPagination V1")
    void shouldReturnPaging_whenRequestingGetWithGivingRequestParam_onFindAllWithPaginationV1() throws Exception {
        // Given
        List<CategoryDTO.Response> responses = List.of(
                CategoryDTO.Response.builder().id(1L).name("Phone").build(),
                CategoryDTO.Response.builder().id(2L).name("Computer").build());
        Page<CategoryDTO.Response> pages = new PageImpl<>(responses, Pageable.ofSize(1), responses.size());
        // When
        when(categoryService.findCategoryWithPagination(anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(pages);
        when(responseService.mapping(any(Supplier.class), any(RateLimiterPlan.class)))
                .thenReturn(ResponseEntity.ok().body(ResponseDTO.builder().data(pages).build()));
        // Then
        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("accept", "application/vnd.lvoxx.app-v1+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                .andExpect(jsonPath("$.data.content[0].name").value("Phone"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[1].name").value("Computer"))
                .andExpect(jsonPath("$.data.content[1].id").value(2));
    }

    // Invalid request version
    @Test
    @DisplayName("Should return bad request when requesting GET with invalid request parameters on findAllWithPagination V1")
    void shouldReturnBadRequest_whenRequestingGetWithInvalidRequestParam_onFindAllWithPaginationV1() {
    }

    // Single category retrieval
    @Test
    @DisplayName("Should return single category when requesting GET with valid request parameters on findById V1")
    void shouldReturnSingleCategory_whenRequestingGetWithGivingRequestParam_onFindByIdV1() {
    }

    // Invalid request version
    @Test
    @DisplayName("Should return not found when requesting GET with non-existent ID on findById V1")
    void shouldReturnNotFound_whenRequestingGetWithNonExistentId_onFindByIdV1() {
    }

    // Category creation
    @Test
    @DisplayName("Should return created category when requesting POST with valid request body on createCategoryByBody V1")
    void shouldReturnCreatedCategory_whenRequestingPostWithGivingRequestBody_onCreateCategoryByBodyV1() {
    }

    // Invalid request version
    @Test
    @DisplayName("Should return bad request when requesting POST with invalid request body on createCategoryByBody V1")
    void shouldReturnBadRequest_whenRequestingPostWithInvalidRequestBody_onCreateCategoryByBodyV1() {
    }

    // Category update
    @Test
    @DisplayName("Should return updated category when requesting PUT with valid request body on updateCategoryByIdAndBody V1")
    void shouldReturnUpdatedCategory_whenRequestingPutWithGivingRequestBody_onUpdateCategoryByIdAndBodyV1() {
    }

    // Invalid request version
    @Test
    @DisplayName("Should return not found when requesting PUT with non-existent ID on updateCategoryByIdAndBody V1")
    void shouldReturnNotFound_whenRequestingPutWithNonExistentId_onUpdateCategoryByIdAndBodyV1() {
    }

    // Category deletion
    @Test
    @DisplayName("Should return no content status when requesting DELETE with valid request parameter on deleteCategoryById V1")
    void shouldReturNoContentStatus_whenRequestingDeleteWithGivingRequestParam_onDeleteCategoryByIdV1() {
    }

    // Invalid request version
    @Test
    @DisplayName("Should return not found when requesting DELETE with non-existent ID on deleteCategoryById V1")
    void shouldReturnNotFound_whenRequestingDeleteWithNonExistentId_onDeleteCategoryByIdV1() {
    }

    // JPA-related error cases
    @Test
    @DisplayName("Should throw RuntimeException when creating category with duplicate name")
    void shouldRuntimeException_whenCreatingCategoryWithDuplicateName() {
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating category to duplicate name")
    void shouldRuntimeException_whenUpdatingCategoryToDuplicateName() {
    }

}
