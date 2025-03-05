package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Category Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class CategoryRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        List<Category> categories = List.of(
                Category.builder().name("Phone").build(),
                Category.builder().name("Fashion").build(),
                Category.builder().name("Computer").build());

        categoryRepository.saveAll(categories);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Should return paginated categories when finding all categories")
    void shouldReturnCategoryPaginationWhenFindingAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        // When
        Page<Category> page = categoryRepository.findPagedCategories(pageable);
        // Then
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent().stream().filter(category -> "Phone".equals(category.getName())).findFirst().get())
                .satisfies(c -> {
                    assertThat(c.getName()).isEqualTo("Phone");
                });
    }

    @Test
    @DisplayName("Should Return Category When Finding By Name")
    void shouldReturnCategoryWhenFindingByName() {
        // When
        Optional<Category> result = categoryRepository.findByName("Phone");
        // Then
        assertThat(result.get()).isNotNull();
        assertThat(result.get().getName()).isEqualTo("Phone");
    }

    @Test
    @DisplayName("Should Return Category When Finding By Id")
    void shouldReturnCategoryWhenFindingById() {
        // Given
        Long existingId = categoryRepository.findAll().stream()
                .filter(c -> "Computer".equals(c.getName()))
                .findFirst()
                .map(Category::getId)
                .orElseThrow(() -> new IllegalStateException("Category not found"));
        // When
        Optional<Category> result = categoryRepository.findById(existingId);
        // Then
        assertThat(result)
                .isPresent()
                .hasValueSatisfying(category -> {
                    assertThat(category.getId()).isEqualTo(existingId);
                    assertThat(category.getName()).isEqualTo("Computer");
                });
    }

    @Test
    @DisplayName("Should Return Empty When Finding By Non-Existent Name")
    void shouldReturnEmptyWhenFindingByNonExistentName() {
        // When
        Optional<Category> result = categoryRepository.findByName("Nothing Here");

        // Then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Should Return Empty When Finding By Non-Existent Id")
    void shouldReturnEmptyWhenFindingByNonExistentId() {
        // When
        Optional<Category> result = categoryRepository.findById(-1L);

        // Then
        assertThat(result).isNotPresent();
    }
}
