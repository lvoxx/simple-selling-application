package com.shitcode.demo1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.repository.CategoryRepository;
import com.shitcode.demo1.service.impl.CategoryServiceImpl;

@DisplayName("Category Blocking Service Tests")
@Tags({
        @Tag("Service"), @Tag("Mock")
})
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Captor
    ArgumentCaptor<Pageable> pagableCaptor;

    @Captor
    ArgumentCaptor<Category> categoryCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<String> nameCaptor;

    @Test
    @DisplayName("Should return category page when retrieving by pagination")
    void shouldReturnCategoryPageWhenFindingByPagination() {
        // Arrange
        Page<Category> mockPage = new PageImpl<Category>(
                List.of(
                        Category.builder().id(1L).name("Phone").build(),
                        Category.builder().id(2L).name("Computer").build()));
        when(categoryRepository.findPagedCategories(any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        Page<CategoryDTO.Response> result = categoryService.findCategoryWithPagination(1, 10, "id", false);
        // Then
        verify(categoryRepository, times(1)).findPagedCategories(pagableCaptor.capture());
        // Assert Captor
        assertThat(pagableCaptor.getValue()).satisfies(page -> {
            assertThat(page.getPageSize()).isEqualTo(10);
            assertThat(page.getPageNumber()).isEqualTo(0);
            assertTrue(page.getSort().isSorted());
        });
        // Assert Result
        assertThat(result.getContent()).first().satisfies(res -> {
            assertThat(res.getId()).isEqualTo(1L);
            assertThat(res.getName()).isEqualTo("Phone");
        });
    }

    @Test
    @DisplayName("Should throw exception when retrieving by pagination with invalid page order")
    void shouldExceptionWhenFindingByPaginationAndInvalidPageOrder() {
        // Arrange
        when(categoryRepository.findPagedCategories(any(Pageable.class)))
                .thenThrow(new RuntimeException("Invalid input"));
        // When
        // Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.findCategoryWithPagination(1, 10, "id", false));
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo("Invalid input");
        verify(categoryRepository, times(1)).findPagedCategories(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return category when retrieving by valid ID")
    void shouldReturnCategoryWhenFindingById() {
        // Arrange
        when(categoryRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(Category.builder().id(1L).name("Phone").build()));
        // When
        CategoryDTO.Response result = categoryService.findCategoryById(1L);
        // Then
        verify(categoryRepository, times(1)).findById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(1L);
        assertThat(result).isNotNull().satisfies(cg -> {
            assertThat(cg.getId()).isEqualTo(1L);
            assertThat(cg.getName()).isEqualTo("Phone");
        });
    }

    @Test
    @DisplayName("Should return category when retrieving by valid name")
    void shouldReturnCategoryWhenFindingByName() {
        // Arrange
        when(categoryRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(Category.builder().id(1L).name("Phone").build()));
        // When
        CategoryDTO.Response result = categoryService.findCategoryByName("Phone");
        // Then
        verify(categoryRepository, times(1)).findByName(nameCaptor.capture());
        assertThat(nameCaptor.getValue()).isEqualTo("Phone");
        assertThat(result).isNotNull().satisfies(cg -> {
            assertThat(cg.getId()).isEqualTo(1L);
            assertThat(cg.getName()).isEqualTo("Phone");
        });
    }

    @Test
    @DisplayName("Should throw exception category when retrieving by non-existent ID")
    void shouldThrowExceptionWhenFindingByNonExistanceId() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        // When
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findCategoryById(1L));
        // Then
        assertThat(ex).isInstanceOf(EntityNotFoundException.class);
        assertThat(ex.getMessage()).isEqualTo("{exception.entity-not-found.category-id}");
    }

    @Test
    @DisplayName("Should throw exception when retrieving by non-existent name")
    void shouldThrowExceptionWhenFindingByNonExistanceName() {
        // Arrange
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.ofNullable(null));
        // When
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findCategoryByName("Phone"));
        // Then
        assertThat(ex).isInstanceOf(EntityNotFoundException.class);
        assertThat(ex.getMessage()).isEqualTo("{exception.entity-not-found.category-name}");
    }

    @Test
    @DisplayName("Should create a new category and return the created category")
    void shouldCreateCategoryAndReturnCreatedCategory_whenCreatingCateogry() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(Category.builder().id(1L).name("Phone").build());
        // When
        CategoryDTO.Request req = CategoryDTO.Request.builder().name("Phone").build();
        CategoryDTO.Response res = categoryService.createCategory(req);
        // Then
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getName()).isEqualTo("Phone");
        assertThat(res).satisfies(c -> {
            assertThat(c.getId()).isEqualTo(1L);
            assertThat(c.getName()).isEqualTo("Phone");
        });
    }

    @Test
    @DisplayName("Should fail to create a category and return an error response")
    void shouldNotCreateCategoryWhenFailureCreatingCategory() {
        // Arrange
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new RuntimeException("Exception on persisting data"));
        // When
        CategoryDTO.Request req = CategoryDTO.Request.builder().name("Phone").build();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(req));
        // Then
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Exception on persisting data");
    }

    @Test
    @DisplayName("Should fail to create a category with duplicate category and return an error response")
    void shouldNotCreateCategoryWithDuplicateCategory_whenCreatingCategory() {
        // Arrange
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        // When
        CategoryDTO.Request req = CategoryDTO.Request.builder().name("Phone").build();
        // Then
        EntityExistsException ex = assertThrows(EntityExistsException.class, () -> categoryService.createCategory(req));
        assertThat(ex).isInstanceOf(EntityExistsException.class);
        assertThat(ex.getMessage()).isEqualTo("{exception.entity-exists.category}");
    }

    @Test
    @DisplayName("Should update an existing category and return the updated category")
    void shouldUpdateCategoryAndReturnUpdatedCategory_whenUpdatingCateogry() {
        // Given
        Optional<Category> oldCtg = Optional.of(Category.builder().id(1L).name("Phone").build());
        Category newCtg = Category.builder().id(1L).name("iPhone").build();
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(newCtg);
        when(categoryRepository.findById(anyLong())).thenReturn(oldCtg);
        // When
        CategoryDTO.Request req = CategoryDTO.Request.builder().name("iPhone").build();
        CategoryDTO.Response result = categoryService.updateCategory(req, 1L);
        // Then
        assertThat(result).isNotNull().satisfies(res -> {
            assertThat(res.getId()).isEqualTo(1L);
            assertThat(res.getName()).isEqualTo("iPhone");
        });
    }

    @Test
    @DisplayName("Should not update category when the original category is not found")
    void shouldNotUpdateCategoryWhenNotFoundOldCategory() {
        // Given
        CategoryDTO.Request newCtg = CategoryDTO.Request.builder().name("iPhone").build();
        // Arrange
        when(categoryRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Category Not Found"));
        // When
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateCategory(newCtg, 1L));
        // Then
        assertThat(ex).isInstanceOf(EntityNotFoundException.class);
        assertThat(ex.getMessage()).isEqualTo("Category Not Found");
    }

    @Test
    @DisplayName("Should fail to update category and return an error response")
    void shouldNotUpdateCategoryWhenFailureUpdatingCategory() {
        // Given
        Optional<Category> oldCtg = Optional.of(Category.builder().id(1L).name("Phone").build());
        CategoryDTO.Request newCtg = CategoryDTO.Request.builder().name("iPhone").build();
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(oldCtg);
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new RuntimeException("Exception on persisting data"));
        // When
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(newCtg, 1L));
        // Then
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Exception on persisting data");
    }

    @Test
    @DisplayName("Should delete the category successfully")
    void shouldDeleteCategoryWhenDeletingById() {
        Long deleteId = 1L;
        Optional<Category> oldCtg = Optional.of(Category.builder().id(1L).name("Phone").build());
        // Arrange
        when(categoryRepository.findById(deleteId)).thenReturn(oldCtg);
        doNothing().when(categoryRepository).deleteById(deleteId);
        // When
        categoryService.deleteCategoryById(deleteId);
        // Then
        verify(categoryRepository, times(1)).deleteById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should not delete category when the original category is not found")
    void shouldNotDeleteCategoryWhenNotFoundOldCategory() {
        // Given
        Long deleteId = 1L;
        // Arrange
        when(categoryRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Category Not Found"));
        // When
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteCategoryById(deleteId));
        // Then
        verify(categoryRepository, times(0)).deleteById(deleteId);
        assertThat(ex).isInstanceOf(EntityNotFoundException.class);
        assertThat(ex.getMessage()).isEqualTo("Category Not Found");
    }

    @Test
    @DisplayName("Should fail to delete category and return an error response")
    void shouldNotDeleteCategoryWhenFailureUpdatingCategory() {
        // Given
        Long deleteId = 1L;
        Optional<Category> oldCtg = Optional.of(Category.builder().id(1L).name("Phone").build());
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(oldCtg);
        doThrow(new RuntimeException("Exception on persisting data")).when(categoryRepository).deleteById(deleteId);
        // When
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.deleteCategoryById(deleteId));
        // Then
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Exception on persisting data");
    }

}
