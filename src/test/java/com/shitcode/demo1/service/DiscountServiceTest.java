package com.shitcode.demo1.service;

import java.util.UUID;

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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.mapper.DiscountMapper;
import com.shitcode.demo1.repository.DiscountRepository;
import com.shitcode.demo1.service.impl.DiscountServiceImpl;

@DisplayName("Discount Service Tests")
@Tags({
        @Tag("Service"), @Tag("Mock")
})
@RunWith(MockitoJUnitRunner.class)
@ExtendWith({ MockitoExtension.class })
public class DiscountServiceTest {

    @InjectMocks
    DiscountServiceImpl discountService;

    @Mock
    DiscountRepository discountRepository;

    @Spy
    DiscountMapper discountMapper = Mappers.getMapper(DiscountMapper.class);

    @Captor
    ArgumentCaptor<Pageable> pagableCaptor;

    @Captor
    ArgumentCaptor<Discount> discountCaptor;

    @Captor
    ArgumentCaptor<UUID> idCaptor;

    @Test
    @DisplayName("Should return ManageResponse when finding by ID")
    void shouldReturnManageResponse_whenFindingById() {

    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when finding by non-existent ID")
    void shouldThrowEntityNotFoundException_whenFindingByNonExistanceId() {

    }

    @Test
    @DisplayName("Should throw RuntimeException when an unknown error occurs")
    void shouldThrowRuntimeException_whenUnexpectedErrorOccurs() {

    }

    @Test
    @DisplayName("Should successfully create a discount")
    void shouldCreateDiscountSuccessfully() {

    }

    @Test
    @DisplayName("Should update discount successfully")
    void shouldUpdateDiscountSuccessfully() {

    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent discount")
    void shouldThrowEntityNotFoundException_whenUpdatingNonExistentDiscount() {

    }

    @Test
    @DisplayName("Should delete discount successfully")
    void shouldDeleteDiscountSuccessfully() {

    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent discount")
    void shouldThrowEntityNotFoundException_whenDeletingNonExistentDiscount() {

    }
}
