package com.shitcode.demo1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;

import com.shitcode.demo1.component.DatabaseLock;
import com.shitcode.demo1.dto.DiscountDTO.DiscountDetailsResponse;
import com.shitcode.demo1.dto.DiscountDTO.ManageRequest;
import com.shitcode.demo1.dto.DiscountDTO.ManageResponse;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.helper.DiscountDateTimeConverter;
import com.shitcode.demo1.mapper.DiscountMapper;
import com.shitcode.demo1.repository.DiscountRepository;
import com.shitcode.demo1.service.impl.DiscountServiceImpl;
import com.shitcode.demo1.utils.DiscountType;

import io.jsonwebtoken.lang.Collections;

@DisplayName("Discount Service Tests")
@Tags({
                @Tag("Service"), @Tag("Mock")
})
@RunWith(MockitoJUnitRunner.class)
@ExtendWith({ MockitoExtension.class })
@Import({ DatabaseLock.class })
public class DiscountServiceTest {

        private static final Logger logger = LoggerFactory.getLogger(DiscountServiceTest.class);

        @InjectMocks
        DiscountServiceImpl discountService;

        @Mock
        DiscountRepository discountRepository;

        @Spy
        DiscountMapper discountMapper = Mappers.getMapper(DiscountMapper.class);

        DatabaseLock databaseLock;

        @Captor
        ArgumentCaptor<Pageable> pagableCaptor;

        @Captor
        ArgumentCaptor<Discount> discountCaptor;

        @Captor
        ArgumentCaptor<UUID> idCaptor;

        OffsetDateTime now = OffsetDateTime.now();
        UUID discountId = UUID.randomUUID();

        @BeforeEach
        void setUp() throws Exception {
                // Manually create a real instance of DatabaseLock
                databaseLock = new DatabaseLock();
                databaseLock.init(); // Ensure cache is initialized

                // Inject it into DiscountServiceImpl
                Field databaseLockField = DiscountServiceImpl.class.getDeclaredField("databaseLock");
                databaseLockField.setAccessible(true);
                databaseLockField.set(discountService, databaseLock);
        }

        Discount newDiscount = Discount.builder()
                        .id(discountId)
                        .title(DiscountType.DAILY_SALES.getFullTitle())
                        .type(DiscountType.DAILY_SALES)
                        .salesPercentAmount(DiscountType.DAILY_SALES.getSalesPercentAmount())
                        .expDate(DiscountDateTimeConverter.convert(DiscountType.DAILY_SALES, now))
                        .products(Collections.emptyList())
                        .build();
        Discount oldDiscount = Discount.builder()
                        .id(discountId)
                        .title(DiscountType.FLASH_SALES.getFullTitle())
                        .type(DiscountType.FLASH_SALES)
                        .salesPercentAmount(DiscountType.FLASH_SALES.getSalesPercentAmount())
                        .expDate(DiscountDateTimeConverter.convert(DiscountType.FLASH_SALES, now))
                        .products(Collections.emptyList())
                        .build();

        @Test
        @DisplayName("Should return DiscountDetailsResponse when finding by ID")
        void shouldReturnDiscountDetailsResponse_whenFindingById() {
                // When
                when(discountRepository.findDetailDiscountById(any(UUID.class))).thenReturn(Optional.of(newDiscount));
                // Then
                DiscountDetailsResponse result = discountService.findById(discountId);
                logger.info(result.toString());
                assertThat(result)
                                .isNotNull()
                                .isInstanceOf(DiscountDetailsResponse.class)
                                .satisfies(res -> {
                                        assertThat(res.getId()).isEqualByComparingTo(discountId);
                                        assertThat(res.getTitle()).isEqualTo(DiscountType.DAILY_SALES.getFullTitle());
                                        assertThat(res.getSalesPercentAmount())
                                                        .isEqualTo(DiscountType.DAILY_SALES.getSalesPercentAmount());
                                        assertThat(res.getExpDate())
                                                        .isAfterOrEqualTo(DiscountDateTimeConverter
                                                                        .convert(DiscountType.DAILY_SALES, now));
                                        assertThat(res.getProducts()).isEmpty();
                                });
                verify(discountRepository, times(1)).findDetailDiscountById(discountId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when finding by non-existent ID")
        void shouldThrowEntityNotFoundException_whenFindingByNonExistanceId() {
                // When
                when(discountRepository.findDetailDiscountById(any(UUID.class))).thenReturn(Optional.empty());
                // Then
                RuntimeException exception = assertThrows(EntityNotFoundException.class,
                                () -> discountService.findById(discountId));
                assertThat(exception).isInstanceOf(EntityNotFoundException.class);
                assertThat(exception.getMessage()).isEqualTo("{exception.entity-not-found.discount-id}");
                verify(discountRepository, times(1)).findDetailDiscountById(discountId);
        }

        @Test
        @DisplayName("Should successfully create a discount")
        void shouldCreateDiscountSuccessfully() {
                // When
                when(discountRepository.save(any(Discount.class))).thenReturn(newDiscount);
                // Then
                assertThat(discountService.create(ManageRequest.builder()
                                .type(DiscountType.DAILY_SALES)
                                .salesPercentAmount(null)
                                .build())).isNotNull()
                                .isInstanceOf(ManageResponse.class)
                                .satisfies(res -> {
                                        assertThat(res.getTitle()).isEqualTo(newDiscount.getTitle());
                                        assertThat(res.getType()).isEqualTo(newDiscount.getType());
                                        assertThat(res.getId()).isEqualTo(newDiscount.getId());
                                        assertThat(res.getExpDate()).isAfterOrEqualTo(newDiscount.getExpDate());
                                        assertThat(res.getSalesPercentAmount())
                                                        .isEqualTo(newDiscount.getSalesPercentAmount());
                                });
                verify(discountRepository, times(1)).save(discountCaptor.capture());
                assertThat(discountCaptor.getValue().getTitle())
                                .isEqualTo(newDiscount.getTitle());
        }

        @Test
        @DisplayName("Should update discount successfully")
        void shouldUpdateDiscountSuccessfully() {
                // When
                when(discountRepository.findById(any(UUID.class))).thenReturn(Optional.of(oldDiscount));
                when(discountRepository.save(any(Discount.class))).thenReturn(newDiscount);
                // Then
                assertThat(discountService.update(ManageRequest.builder()
                                .type(newDiscount.getType())
                                .salesPercentAmount(null)
                                .build(), discountId))
                                .isNotNull()
                                .isInstanceOf(ManageResponse.class)
                                .satisfies(res -> {
                                        assertThat(res.getTitle()).isEqualTo(newDiscount.getTitle());
                                        assertThat(res.getType()).isEqualTo(newDiscount.getType());
                                        assertThat(res.getId()).isEqualTo(newDiscount.getId());
                                        assertThat(res.getExpDate()).isAfterOrEqualTo(newDiscount.getExpDate());
                                        assertThat(res.getSalesPercentAmount())
                                                        .isEqualTo(newDiscount.getSalesPercentAmount());
                                });
                verify(discountRepository, times(1)).save(discountCaptor.capture());
                assertThat(discountCaptor.getValue().getTitle())
                                .isEqualTo(newDiscount.getTitle());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when updating non-existent discount")
        void shouldThrowEntityNotFoundException_whenUpdatingNonExistentDiscount() {
                // When
                when(discountRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
                // Then
                RuntimeException ex = assertThrows(EntityNotFoundException.class,
                                () -> discountService.update(ManageRequest.builder()
                                                .type(newDiscount.getType())
                                                .salesPercentAmount(null)
                                                .build(), discountId));
                assertThat(ex).isNotNull()
                                .isInstanceOf(EntityNotFoundException.class)
                                .satisfies(e -> {
                                        assertThat(e.getMessage())
                                                        .isEqualTo("{exception.entity-not-found.discount-id}");
                                });
        }

        @Test
        @DisplayName("Should delete discount successfully")
        void shouldDeleteDiscountSuccessfully() {
                // When
                when(discountRepository.findById(any(UUID.class))).thenReturn(Optional.of(oldDiscount));
                Mockito.doNothing().when(discountRepository).deleteById(any(UUID.class));
                // Then
                discountService.delete(discountId);
                Mockito.verify(discountRepository, times(1)).deleteById(discountId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when deleting non-existent discount")
        void shouldThrowEntityNotFoundException_whenDeletingNonExistentDiscount() {
                // When
                when(discountRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
                // Then
                RuntimeException ex = assertThrows(EntityNotFoundException.class,
                                () -> discountService.delete(discountId));
                assertThat(ex.getMessage()).isEqualTo("{exception.entity-not-found.discount-id}");
        }
}
