package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.helper.DiscountDateTimeConverter;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;
import com.shitcode.demo1.utils.DiscountType;

import jakarta.persistence.EntityManager;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Discount Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class DiscountRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    OffsetDateTime time = OffsetDateTime.now();

    @BeforeEach
    void setUp() {
        List<Category> categories = categoryRepository.saveAllAndFlush(List.of(
                Category.builder().name("Phone").build(),
                Category.builder().name("Fashion").build(),
                Category.builder().name("Computer").build()));

        List<Discount> discounts = discountRepository.saveAllAndFlush(List.of(
                // Example 1: FLASH_SALES Discount
                Discount.builder()
                        .title("Flash Sales Discount")
                        .types(List.of(DiscountType.FLASH_SALES))
                        .salesPercentAmount(20.0)
                        .expDate(DiscountDateTimeConverter.convert(DiscountType.FLASH_SALES, time)) // Expires in 2
                        // hours
                        .build(),
                // Example 2: DAILY_SALES Discount
                Discount.builder()
                        .title("Daily Sales Discount")
                        .types(List.of(DiscountType.DAILY_SALES))
                        .salesPercentAmount(10.0)
                        .expDate(DiscountDateTimeConverter.convert(DiscountType.DAILY_SALES, time)) // Expires in 12
                        // hours
                        .build()));

        productRepository.saveAllAndFlush(List.of(
                Product.builder()
                        .name("Smartphone")
                        .inStockQuantity(50)
                        .inSellQuantity(30)
                        .price(BigDecimal.valueOf(499.99))
                        .category(categories.get(0))
                        .discount(discounts.get(0))
                        .build(),

                Product.builder()
                        .name("T-Shirt")
                        .inStockQuantity(100)
                        .inSellQuantity(70)
                        .price(BigDecimal.valueOf(19.99))
                        .category(categories.get(1))
                        .discount(discounts.get(0))
                        .build(),

                Product.builder()
                        .name("Laptop")
                        .inStockQuantity(20)
                        .inSellQuantity(5)
                        .price(BigDecimal.valueOf(999.99))
                        .category(categories.get(2))
                        .discount(discounts.get(1))
                        .build()));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        discountRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return entities when finding by title")
    void shouldReturnEntitiesWhenFindingByTitle() {
        // When
        List<Discount> discounts = discountRepository.findEntitiesByTitle("Sales Discount");
        // Then
        assertThat(discounts)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Discount::getTitle)
                .containsExactlyInAnyOrder("Flash Sales Discount", "Daily Sales Discount");
    }

    @Test
    @DisplayName("Should return a single entity when finding by title")
    void shouldReturnEntityWhenFindingByTitle() {
        // When
        Optional<Discount> discount = discountRepository.findEntityByTitle("Flash Sales Discount");
        // Then
        assertThat(discount).isPresent().hasValueSatisfying(d -> {
            assertThat(d.getTitle()).isEqualTo("Flash Sales Discount");
            assertThat(d.getTypes()).isEqualTo(List.of(DiscountType.FLASH_SALES));
            assertThat(d.getExpDate()).isAfterOrEqualTo(time.plusHours(2));
        })
                .withFailMessage("Expected not null discount with title \"Flash Sales Discount\" but null");
    }

    @Test
    @DisplayName("Should return a list of entities when finding by expiration date between a range")
    void shouldReturnListOfEntities_whenFindingByExpDateBetween() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime beforeTime = now.minusHours(1);
        OffsetDateTime afterTime = now.plusHours(6);
        // When
        List<Discount> discounts = discountRepository.findByExpDateBetween(beforeTime, afterTime);
        // Then
        assertThat(discounts)
                .isNotEmpty()
                .hasSize(1)
                .extracting(Discount::getTitle)
                .containsExactlyInAnyOrder("Flash Sales Discount");

    }

    @Test
    @DisplayName("Should return paging when finding by title and expiration date between a range")
    void shouldReturnPaging_whenFindingByTitleAndExpDateBetween() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime beforeTime = now.minusHours(1);
        OffsetDateTime afterTime = now.plusDays(1);
        Pageable pageable = PaginationProvider.build(1, 10, "title", false);
        // When
        Page<Discount> page = discountRepository.findByTitleAndExpDateBetween("", beforeTime, afterTime, pageable);
        // Then
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getContent())
                .isNotEmpty()
                .extracting(Discount::getTitle)
                .containsExactlyInAnyOrder("Flash Sales Discount", "Daily Sales Discount");
    }

    @Test
    @DisplayName("Should remove expired discounts from products when revoking by discount ID")
    void shouldremoveExpiredDiscountsFromProductsWhenRevingByDiscountId() {
        // Given
        UUID id = discountRepository.findEntityByTitle("Flash Sales Discount").get().getId();
        List<Long> productIdNeedToRemoveDiscount = productRepository.findAll()
                .stream()
                .filter(p -> p.getDiscount() != null && p.getDiscount().getId().equals(id))
                .map(Product::getId)
                .collect(Collectors.toList());
        // When
        discountRepository.removeExpiredDiscountsFromProducts(id);

        // Then
        List<Product> products = productRepository.findAllById(productIdNeedToRemoveDiscount);
        assertThat(products)
                .isNotEmpty()
                .extracting(Product::getDiscount)
                .containsOnlyNulls();
    }

}
