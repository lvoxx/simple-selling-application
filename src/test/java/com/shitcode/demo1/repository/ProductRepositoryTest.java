package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.repository.dto.ProductCategoryDiscountDTO;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;
import com.shitcode.demo1.utils.DiscountType;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class ProductRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    DiscountRepository discountRepository;

    @BeforeEach
    void setUp() {
        List<Category> categories = categoryRepository.saveAll(List.of(
                Category.builder().name("Phone").build(),
                Category.builder().name("Fashion").build(),
                Category.builder().name("Computer").build()));

        Discount discount = discountRepository.saveAndFlush(Discount.builder()
                .title("Summer Sale")
                .type(DiscountType.HOLIDAY_SALES)
                .salesPercentAmount(15.0)
                .expDate(OffsetDateTime.now().plusDays(30))
                .build());

        List<Product> products = List.of(
                Product.builder()
                        .name("Smartphone")
                        .inStockQuantity(50)
                        .inSellQuantity(30)
                        .price(BigDecimal.valueOf(499.99))
                        .category(categories.get(0))
                        .discount(discount)
                        .build(),

                Product.builder()
                        .name("T-Shirt")
                        .inStockQuantity(100)
                        .inSellQuantity(70)
                        .price(BigDecimal.valueOf(19.99))
                        .category(categories.get(1))
                        .discount(discount)
                        .build(),

                Product.builder()
                        .name("Laptop")
                        .inStockQuantity(20)
                        .inSellQuantity(5)
                        .price(BigDecimal.valueOf(999.99))
                        .category(categories.get(2))
                        .discount(discount)
                        .build());
        productRepository.saveAll(products);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should return the product when finding by an existing product name")
    void shouldReturnProductWhenFindingByProductName() {
        // When
        Optional<Product> product = productRepository.findByName("Smartphone");
        // Then
        assertThat(product).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getName()).isEqualTo("Smartphone");
            assertThat(p.getInStockQuantity()).isEqualTo(50);
            assertThat(p.getInSellQuantity()).isEqualTo(30);
            assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(499.99));
            assertThat(p.getCategory().getName()).isEqualTo("Phone");
        });
    }

    @Test
    @Order(1)
    @DisplayName("Should return the product when finding by an existing product ID")
    void shouldReturnProductWhenFindingByProductId() {
        Long existingId = productRepository.findAll().stream()
                .filter(p -> "Smartphone".equals(p.getName()))
                .findFirst()
                .map(Product::getId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        // When
        Optional<Product> product = productRepository.findById(existingId);
        // Then
        assertThat(product).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getName()).isEqualTo("Smartphone");
            assertThat(p.getInStockQuantity()).isEqualTo(50);
            assertThat(p.getInSellQuantity()).isEqualTo(30);
            assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(499.99));
            assertThat(p.getCategory().getName()).isEqualTo("Phone");
        });
    }

    @Test
    @Order(1)
    @DisplayName("Should return empty when finding by a non-existent product name")
    void shouldReturnEmptyWhenFindingByNonExistProductName() {
        // When
        Optional<Product> product = productRepository.findByName("Dummy");
        // Then
        assertThat(product).isNotPresent();
    }

    @Test
    @Order(1)
    @DisplayName("Should return empty when finding by a non-existent product ID")
    void shouldReturnEmptyWhenFindingByNonExistProductId() {
        // When
        Optional<Product> product = productRepository.findById(-1L);
        // Then
        assertThat(product).isNotPresent();
    }

    @Test
    @Order(2)
    @DisplayName("Should return all products when listing by an existing category name")
    void shouldReturnAllProductsWhenListingByCategoryName() {
        // When
        List<Product> products = productRepository.findProductsByCategoryName("Phone");
        // Then
        assertThat(products).hasSize(1);
        assertThat(products).isNotEmpty().first()
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo("Smartphone");
                    assertThat(p.getInStockQuantity()).isEqualTo(50);
                    assertThat(p.getInSellQuantity()).isEqualTo(30);
                    assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(499.99));
                    assertThat(p.getCategory().getName()).isEqualTo("Phone");
                });
    }

    @Test
    @Order(3)
    @DisplayName("Should return all products with category")
    void shouldReturnAllProductsWithCategory() {
        // When
        List<Product> products = productRepository.findAllProductsWithCategory();
        // Then
        assertThat(products).hasSize(3);
    }

    @Test
    @Order(4)
    @DisplayName("Should return 1 pages with page size 1 when finding category name")
    void shouldReturn1PagesWithPageSize1WhenFindingCategoryName() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        // When
        Page<Product> page = productRepository.findPagedProductsByCategoryName("Phone", pageable);
        // Then
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst())
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo("Smartphone");
                    assertThat(p.getCategory().getName()).isEqualTo("Phone");
                });
    }

    @Test
    @Order(4)
    @DisplayName("Should return 3 pages with page size 1 when listing all products")
    void shouldReturn3PagesWithPageSize1WhenListingAllProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        // When
        Page<Product> page = productRepository.findPagedAllProductsWithCategory(pageable);
        // Then
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return product with discount and category when finding by product id")
    void shouldReturnProductWithDiscountAndCategory_WhenFindingByProductId() {
        // Given
        Long id = 1L;
        // When
        Optional<ProductCategoryDiscountDTO> product = productRepository
                .findProductWithDiscountAndCategoryById(id);
        // Then
        LogPrinter.printLog(Type.INFO, LogPrinter.Flag.REPOSITORY_FLAG, product.get().toString());

        assertThat(product.get()).isNotNull().satisfies(p -> {
            assertThat(p.getCategoryId()).isEqualTo(1L);
            assertThat(p.getCategoryName()).isEqualTo("Phone");
            assertThat(p.getProductName()).isEqualTo("Smartphone");
        });
    }
}
