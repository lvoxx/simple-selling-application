package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.helper.PrettyPrintList;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Product Interaction Repository Tests")
@Tags({
                @Tag("Repository"), @Tag("No Mock")
})
public class ProductInteractionRepositoryTest extends AbstractRepositoryTest {

        @SuppressWarnings("unused")
        private static final Logger logger = LoggerFactory.getLogger(ProductInteractionRepositoryTest.class);

        @Autowired
        ProductRepository productRepository;

        @Autowired
        CategoryRepository categoryRepository;

        @Autowired
        ProductInteractionRepository productInteractionRepository;

        @BeforeEach
        void setUp() {
                List<Category> categories = categoryRepository.saveAllAndFlush(List.of(
                                Category.builder().name("Phone").build(),
                                Category.builder().name("Fashion").build(),
                                Category.builder().name("Computer").build()));

                List<Product> products = List.of(
                                Product.builder()
                                                .name("Smartphone")
                                                .inStockQuantity(50)
                                                .inSellQuantity(30)
                                                .price(BigDecimal.valueOf(499.99))
                                                .category(categories.get(0))
                                                .build(),

                                Product.builder()
                                                .name("T-Shirt")
                                                .inStockQuantity(100)
                                                .inSellQuantity(70)
                                                .price(BigDecimal.valueOf(19.99))
                                                .category(categories.get(1))
                                                .build(),

                                Product.builder()
                                                .name("Laptop")
                                                .inStockQuantity(20)
                                                .inSellQuantity(5)
                                                .price(BigDecimal.valueOf(999.99))
                                                .category(categories.get(2))
                                                .build());
                Map<String, Long> productIdMap = productRepository.saveAllAndFlush(products)
                                .stream()
                                .collect(Collectors.toMap(Product::getName, Product::getId));

                // Setup product interactions with associated product IDs
                List<ProductInteraction> interactions = List.of(
                                ProductInteraction.builder()
                                                .productId(productIdMap.get("T-Shirt"))
                                                .username("New Visitor")
                                                .onTime(LocalDateTime.now().minusDays(2))
                                                .locateAt("Homepage")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("Laptop"))
                                                .username("Returning Customer")
                                                .onTime(LocalDateTime.now().minusHours(6))
                                                .locateAt("Search Bar")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("Laptop"))
                                                .username("Active Shopper")
                                                .onTime(LocalDateTime.now().minusHours(3))
                                                .locateAt("Product Page")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("T-Shirt"))
                                                .username("Loyal Member")
                                                .onTime(LocalDateTime.now().minusMinutes(45))
                                                .locateAt("Product Page")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("T-Shirt"))
                                                .username("Cart Abandoner")
                                                .onTime(LocalDateTime.now().minusMinutes(30))
                                                .locateAt("Cart Page")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("Laptop"))
                                                .username("Verified Buyer")
                                                .onTime(LocalDateTime.now().minusMinutes(15))
                                                .locateAt("Checkout Page")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("Laptop"))
                                                .username("Premium Subscriber")
                                                .onTime(LocalDateTime.now().minusMinutes(10))
                                                .locateAt("Checkout Page")
                                                .build(),

                                ProductInteraction.builder()
                                                .productId(productIdMap.get("T-Shirt"))
                                                .username("In-Store Shopper")
                                                .onTime(LocalDateTime.now().minusMinutes(5))
                                                .locateAt("Pickup Store")
                                                .build());

                productInteractionRepository.saveAll(interactions);
        }

        @AfterEach
        void tearDown() {
                productInteractionRepository.deleteAll();
        }

        @Test
        @DisplayName("Should return entities when finding exaggeration group by product name")
        void shouldReturnEntities_whenFindingExaggerationGroupByProductName() {
                // When
                List<Object[]> results = productInteractionRepository
                                .findExaggerationGroupByProductName("");
                PrettyPrintList.printPrettyJson(results);
                // Then
                assertThat(results).hasSize(2);
                assertThat(results).containsSubsequence(new Object[] { "T-Shirt", 4L });
                assertThat(results).containsSubsequence(new Object[] { "Laptop", 4L });
        }

        @Test
        @DisplayName("Should return entities when searching by username")
        void shouldReturnEntities_whenFindingByUsername() {
                // When
                List<ProductInteraction> res = productInteractionRepository.findByUsernameContaining("Shopper");
                // Then
                assertThat(res).hasSize(2)
                                .extracting(ProductInteraction::getUsername)
                                .containsExactlyInAnyOrder("In-Store Shopper", "Active Shopper");
        }

        @Test
        @DisplayName("Should return entities when searching by location")
        void shouldReturnEntities_whenFindingByLocateAt() {
                // When
                List<ProductInteraction> res = productInteractionRepository.findByLocateAtContaining("Page");
                // Then
                assertThat(res).hasSize(5)
                                .extracting(ProductInteraction::getLocateAt)
                                .containsAnyOf("Product Page",
                                                "Cart Page",
                                                "Checkout Page");
        }

        @Test
        @DisplayName("Should return paginated results with joined Product and Category tables when searching by time range")
        void shouldReturnPageWithJoiningProductAndCategoryTable_whenFindingByPageByTimeBetween() {
                // When
                // Then
        }

}
