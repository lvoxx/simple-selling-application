package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;
import com.shitcode.demo1.utils.InteractionEvent;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Product Interaction Repository Tests")
@Tags({
        @Tag("Repository"), @Tag("No Mock")
})
public class ProductInteractionRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    ProductInteractionRepository productInteractionRepository;

    @BeforeEach
    void setUp() {
        productInteractionRepository.deleteAll(); // Clear existing data

        productInteractionRepository.saveAll(List.of(
                ProductInteraction.builder()
                        .eventStage(InteractionEvent.PICK_CATEGORY)
                        .productName("Electronics")
                        .userStage("Guest User")
                        .onTime(LocalDateTime.now().minusDays(2))
                        .locateAt("Homepage")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.SEARCH_PRODUCT)
                        .productName("Smartphone X")
                        .userStage("Registered User")
                        .onTime(LocalDateTime.now().minusHours(6))
                        .locateAt("Search Bar")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.VIEW_PRODUCT)
                        .productName("Wireless Earbuds")
                        .userStage("Registered User")
                        .onTime(LocalDateTime.now().minusHours(3))
                        .locateAt("Product Page")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.VIEW_AND_ADD_TO_CART)
                        .productName("Gaming Laptop")
                        .userStage("Premium Member")
                        .onTime(LocalDateTime.now().minusMinutes(45))
                        .locateAt("Product Page")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.CART_TO_PURCHASE)
                        .productName("Smartwatch 2")
                        .userStage("Registered User")
                        .onTime(LocalDateTime.now().minusMinutes(30))
                        .locateAt("Cart Page")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.PAY_BY_CARD)
                        .productName("Noise Cancelling Headphones")
                        .userStage("Verified Buyer")
                        .onTime(LocalDateTime.now().minusMinutes(15))
                        .locateAt("Checkout Page")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.PAY_BY_PAYPAL)
                        .productName("Gaming Mouse")
                        .userStage("Premium Member")
                        .onTime(LocalDateTime.now().minusMinutes(10))
                        .locateAt("Checkout Page")
                        .build(),

                ProductInteraction.builder()
                        .eventStage(InteractionEvent.PAY_BY_CASH)
                        .productName("Mechanical Keyboard")
                        .userStage("Guest User")
                        .onTime(LocalDateTime.now().minusMinutes(5))
                        .locateAt("Pickup Store")
                        .build()));
    }

    @AfterEach
    void tearDown() {
        productInteractionRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return entities when finding by product name")
    void shouldReturnEntities_whenFindingByProductName() {
        // When
        List<ProductInteraction> results = productInteractionRepository.findByProductName("Smartphone X");

        // Then
        assertThat(results)
                .isNotEmpty()
                .allMatch(pi -> "Smartphone X".equals(pi.getProductName()));
    }

    @Test
    @DisplayName("Should return entities when finding by event stage")
    void shouldReturnEntities_whenFindingByEventStage() {
        // When
        List<ProductInteraction> results = productInteractionRepository
                .findByEventStage(InteractionEvent.SEARCH_PRODUCT);

        // Then
        assertThat(results)
                .isNotEmpty()
                .allMatch(pi -> pi.getEventStage() == InteractionEvent.SEARCH_PRODUCT);
    }

    @Test
    @DisplayName("Should return entities when finding by user stage")
    void shouldReturnEntities_whenFindingByUserStage() {
        // When
        List<ProductInteraction> results = productInteractionRepository.findByUserStage("Guest User");

        // Then
        assertThat(results)
                .isNotEmpty()
                .allMatch(pi -> "Guest User".equals(pi.getUserStage()));
    }

    @Test
    @DisplayName("Should return entities when finding by onTime")
    void shouldReturnEntities_whenFindingByOnTime() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now().minusHours(6);
        LocalDateTime endTime = LocalDateTime.now();

        // When
        List<ProductInteraction> results = productInteractionRepository.findByTime(timestamp, endTime);

        // Then
        assertThat(results)
                .isNotEmpty()
                .allMatch(pi -> !pi.getOnTime().isBefore(timestamp) && !pi.getOnTime().isAfter(endTime)); // Check range
    }

    @Test
    @DisplayName("Should return entities when finding by location")
    void shouldReturnEntities_whenFindingByLocateAt() {
        // When
        List<ProductInteraction> results = productInteractionRepository.findByLocateAt("Search Bar");

        // Then
        assertThat(results)
                .isNotEmpty()
                .allMatch(pi -> "Search Bar".equals(pi.getLocateAt()));
    }

    @Test
    @DisplayName("Should return concatenated event details when searching by event stage and time range")
    void shouldReturnConcatenatedDetails_whenSearchingByEventStageAndTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(3);
        LocalDateTime endTime = LocalDateTime.now();

        // When
        List<String> results = productInteractionRepository.searchByFormattedColumnAndTimeRange(
                InteractionEvent.SEARCH_PRODUCT, startTime, endTime);

        // Then
        assertThat(results).isNotEmpty();
    }
}
