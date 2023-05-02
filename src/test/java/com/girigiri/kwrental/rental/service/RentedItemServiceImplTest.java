package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RentedItemServiceImplTest {

    @Mock
    private RentalSpecRepository rentalSpecRepository;

    @InjectMocks
    private RentedItemServiceImpl rentedItemService;

    @Test
    @DisplayName("대여 중인 품목의 자산번호를 조회한다.")
    void getRentedPropertyNumbers() {
        // given
        given(rentalSpecRepository.findRentedRentalSpecs(any(), any()))
                .willReturn(Set.of(RentalSpecFixture.builder().propertyNumber("12345678").build()));

        // when
        final Set<String> rentedPropertyNumbers = rentedItemService.getRentedPropertyNumbers(1L, LocalDateTime.now());

        // then
        assertThat(rentedPropertyNumbers).containsOnly("12345678");
    }
}