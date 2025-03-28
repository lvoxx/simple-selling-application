package com.shitcode.demo1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.config.MessagesConfig;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.WorkerBusyException;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import lombok.SneakyThrows;

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
@ImportAutoConfiguration(classes = { MessagesConfig.class })
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
        @SneakyThrows
        @DisplayName("Should return paging when requesting GET with valid request parameters on findAllWithPagination V1")
        void shouldReturnPaging_whenRequestingGetWithGivingRequestParam_onFindAllWithPaginationV1() {
                // Given
                List<CategoryDTO.Response> responses = List.of(
                                CategoryDTO.Response.builder().id(1L).name("Phone").build(),
                                CategoryDTO.Response.builder().id(2L).name("Computer").build());
                Page<CategoryDTO.Response> pages = new PageImpl<>(responses, Pageable.ofSize(1), responses.size());
                // When
                when(categoryService.findCategoryWithPagination(anyInt(), anyInt(), anyString(), anyBoolean()))
                                .thenReturn(pages);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
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
        @SneakyThrows
        @DisplayName("Should return bad request when requesting GET with invalid request parameters on findAllWithPagination V1")
        void shouldReturnBadRequest_whenRequestingGetWithInvalidRequestParam_onFindAllWithPaginationV1() {
                // Given
                var size = "abc"; // expected (int)
                var page = "xyz"; // expected (int)
                var asc = "Not"; // expected (boolean)
                // When
                // Then
                // Invalid - size
                mockMvc.perform(get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("s", size))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.message").value(
                                                "Method parameter 's': Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \""
                                                                + size + "\""))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // Invalid - page
                mockMvc.perform(get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("p", page))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.message").value(
                                                "Method parameter 'p': Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \""
                                                                + page + "\""))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // Invalid - asc
                mockMvc.perform(get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("a", asc))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.message").value(
                                                "Method parameter 'a': Failed to convert value of type 'java.lang.String' to required type 'boolean'; Invalid boolean value ["
                                                                + asc + "]"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
        }

        // Single category retrieval
        @Test
        @SneakyThrows
        @DisplayName("Should return single category when requesting GET with valid request parameters on findById V1")
        void shouldReturnSingleCategory_whenRequestingGetWithGivingRequestParam_onFindByIdV1() {
                // Given
                Long ctgId = 1L;
                CategoryDTO.Response response = CategoryDTO.Response.builder().id(ctgId).name("Phone").build();
                // When
                when(categoryService.findCategoryById(anyLong())).thenReturn(response);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenReturn(ResponseEntity.ok().body(ResponseDTO.builder().data(response).build()));
                // Then
                mockMvc.perform(get("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.data.id").value(response.getId()))
                                .andExpect(jsonPath("$.data.name").value(response.getName()));
        }

        // Invalid request version
        @Test
        @SneakyThrows
        @DisplayName("Should return not found when requesting GET with non-existent ID on findById V1")
        void shouldReturnNotFound_whenRequestingGetWithNonExistentId_onFindByIdV1() {
                // Given
                Long ctgId = -1L;
                EntityNotFoundException ex = new EntityNotFoundException("Not found category with Id " + ctgId);
                // When
                when(categoryService.findCategoryById(anyLong()))
                                .thenThrow(ex);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class))).thenThrow(ex);
                // Then
                mockMvc.perform(get("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json"))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        // Category creation
        @Test
        @SneakyThrows
        @DisplayName("Should return created category when requesting POST with valid request body on createCategoryByBody V1")
        void shouldReturnCreatedCategory_whenRequestingPostWithGivingRequestBody_onCreateCategoryByBodyV1() {
                // Given
                CategoryDTO.Request request = CategoryDTO.Request.builder().name("Phone").build();
                CategoryDTO.Response response = CategoryDTO.Response.builder().id(1L).name("Phone").build();
                // When
                when(categoryService.createCategory(any(CategoryDTO.Request.class)))
                                .thenReturn(response);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenReturn(new ResponseEntity<>(ResponseDTO.builder().data(response).build(),
                                                HttpStatus.CREATED));
                // Then
                mockMvc.perform(post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.data.id").value(1L))
                                .andExpect(jsonPath("$.data.name").value("Phone"));
        }

        // Invalid request version
        @Test
        @SneakyThrows
        @DisplayName("Should return bad request when requesting POST with invalid request body on createCategoryByBody V1")
        void shouldReturnBadRequest_whenRequestingPostWithInvalidRequestBody_onCreateCategoryByBodyV1() {
                // Given
                var requestBlank = CategoryDTO.Request.builder().name("").build();
                var requestOutOfSize = CategoryDTO.Request.builder()
                                .name("dummydummydummydummydummydummydummydummydummydummydummydummy.").build();
                // When
                // Then
                // Invalid - blank
                mockMvc.perform(post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(requestBlank)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("name"))
                                .andExpect(jsonPath("$.errors[0].message").value("Category name cannot be blank."))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // Invalid - Out of size
                mockMvc.perform(post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(requestOutOfSize)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("name"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Category name must not exceed 60 characters."))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
        }

        // Category update
        @Test
        @SneakyThrows
        @DisplayName("Should return updated category when requesting PUT with valid request body on updateCategoryByIdAndBody V1")
        void shouldReturnUpdatedCategory_whenRequestingPutWithGivingRequestBody_onUpdateCategoryByIdAndBodyV1() {
                // Given
                Long ctgId = 1L;
                CategoryDTO.Request request = CategoryDTO.Request.builder().name("Phone").build();
                CategoryDTO.Response response = CategoryDTO.Response.builder().id(1L).name("Computer").build();
                // When
                when(categoryService.updateCategory(any(CategoryDTO.Request.class), anyLong()))
                                .thenReturn(response);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenReturn(new ResponseEntity<>(ResponseDTO.builder().data(response).build(),
                                                HttpStatus.OK));
                // Then
                mockMvc.perform(put("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.data.id").value(response.getId()))
                                .andExpect(jsonPath("$.data.name").value(response.getName()));
        }

        // Invalid request version
        @Test
        @SneakyThrows
        @DisplayName("Should return not found when requesting PUT with non-existent ID on updateCategoryByIdAndBody V1")
        void shouldReturnNotFound_whenRequestingPutWithNonExistentId_onUpdateCategoryByIdAndBodyV1() {
                // Given
                Long ctgId = 1L;
                CategoryDTO.Request request = CategoryDTO.Request.builder().name("Phone").build();
                EntityNotFoundException ex = new EntityNotFoundException("Not found category with Id " + ctgId);
                // When
                when(categoryService.updateCategory(any(CategoryDTO.Request.class), anyLong()))
                                .thenThrow(ex);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class))).thenThrow(ex);
                // Then
                mockMvc.perform(put("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        // Category deletion
        @Test
        @SneakyThrows
        @DisplayName("Should return no content status when requesting DELETE with valid request parameter on deleteCategoryById V1")
        void shouldReturNoContentStatus_whenRequestingDeleteWithGivingRequestParam_onDeleteCategoryByIdV1() {
                // Given
                Long ctgId = 1L;
                // When
                doNothing().when(categoryService).deleteCategoryById(anyLong());
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenReturn(new ResponseEntity<>(
                                                ResponseDTO.builder().data(ResponseEntity.noContent().build()).build(),
                                                HttpStatus.NO_CONTENT));
                // Then
                mockMvc.perform(delete("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json"))
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }

        // Invalid request version
        @Test
        @SneakyThrows
        @DisplayName("Should return not found when requesting DELETE with non-existent ID on deleteCategoryById V1")
        void shouldReturnNotFound_whenRequestingDeleteWithNonExistentId_onDeleteCategoryByIdV1() {
                /// Given
                Long ctgId = 1L;
                EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
                // When
                doThrow(ex).when(categoryService).deleteCategoryById(anyLong());
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(ex);
                // Then
                mockMvc.perform(delete("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json"))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        // JPA-related error cases
        @Test
        @SneakyThrows
        @DisplayName("Should throw EntityExistsException when creating category with duplicate name")
        void shouldRuntimeException_whenCreatingCategoryWithDuplicateName() {
                // Given
                CategoryDTO.Request request = CategoryDTO.Request.builder().name("Phone").build();
                EntityExistsException ex = new EntityExistsException();
                // When
                when(categoryService.createCategory(any(CategoryDTO.Request.class)))
                                .thenThrow(ex);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(ex);
                // Then
                mockMvc.perform(post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isConflict())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"));
        }

        @Test
        @SneakyThrows
        @DisplayName("Should throw WorkerBusyException when updating category to server busy")
        void shouldWorkerBusyException_whenUpdatingCategoryToServerBusy() {
                // Given
                Long ctgId = 1L;
                CategoryDTO.Request request = CategoryDTO.Request.builder().name("Phone").build();
                WorkerBusyException ex = new WorkerBusyException();
                // When
                when(categoryService.createCategory(any(CategoryDTO.Request.class)))
                                .thenThrow(ex);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(ex);
                // Then
                mockMvc.perform(put("/categories/{id}", ctgId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"));
        }

}
