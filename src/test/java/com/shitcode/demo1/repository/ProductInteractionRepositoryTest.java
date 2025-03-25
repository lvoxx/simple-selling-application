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

        // @Test
        // @DisplayName("Should return entities when finding by event stage")
        // void shouldReturnEntities_whenFindingByEventStage() {
        // // When
        // List<ProductInteraction> results = productInteractionRepository
        // .findByEventStage(InteractionEvent.SEARCH_PRODUCT);

        // // Then
        // assertThat(results)
        // .isNotEmpty()
        // .allMatch(pi -> pi.getEventStage() == InteractionEvent.SEARCH_PRODUCT);
        // }

        // @Test
        // @DisplayName("Should return entities when finding by user stage")
        // void shouldReturnEntities_whenFindingByUserStage() {
        // // When
        // List<ProductInteraction> results =
        // productInteractionRepository.findByUserStage("Guest User");

        // // Then
        // assertThat(results)
        // .isNotEmpty()
        // .allMatch(pi -> "Guest User".equals(pi.getUserStage()));
        // }

        // @Test
        // @DisplayName("Should return entities when finding by onTime")
        // void shouldReturnEntities_whenFindingByOnTime() {
        // // Given
        // LocalDateTime timestamp = LocalDateTime.now().minusHours(6);
        // LocalDateTime endTime = LocalDateTime.now();

        // // When
        // List<ProductInteraction> results =
        // productInteractionRepository.findByTime(timestamp, endTime);

        // // Then
        // assertThat(results)
        // .isNotEmpty()
        // .allMatch(pi -> !pi.getOnTime().isBefore(timestamp)
        // && !pi.getOnTime().isAfter(endTime)); // Check range
        // }

        // @Test
        // @DisplayName("Should return entities when finding by location")
        // void shouldReturnEntities_whenFindingByLocateAt() {
        // // When
        // List<ProductInteraction> results =
        // productInteractionRepository.findByLocateAt("Search Bar");

        // // Then
        // assertThat(results)
        // .isNotEmpty()
        // .allMatch(pi -> "Search Bar".equals(pi.getLocateAt()));
        // }

        // @Test
        // @DisplayName("Should return concatenated event details when searching by
        // event stage and time range")
        // void shouldReturnConcatenatedDetails_whenSearchingByEventStageAndTimeRange()
        // {
        // // Given
        // LocalDateTime startTime = LocalDateTime.now().minusDays(3);
        // LocalDateTime endTime = LocalDateTime.now();

        // // When
        // List<String> results =
        // productInteractionRepository.searchByFormattedColumnAndTimeRange(
        // InteractionEvent.SEARCH_PRODUCT, startTime, endTime);

        // // Then
        // assertThat(results).isNotEmpty();
        // }
}
