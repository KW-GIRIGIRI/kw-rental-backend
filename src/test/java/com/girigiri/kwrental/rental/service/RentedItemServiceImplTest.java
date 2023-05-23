package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
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
                .willReturn(Set.of(EquipmentRentalSpecFixture.builder().propertyNumber("12345678").build()));

        // when
        final Set<String> rentedPropertyNumbers = rentedItemService.getRentedPropertyNumbers(1L, LocalDateTime.now());

        // then
        assertThat(rentedPropertyNumbers).containsOnly("12345678");
    }

    @Test
    @DisplayName("특정 기간에 자산 번호에 해당하는 대여 상태를 조회한다.")
    void getRentalCountsByPropertyNumbersBetweenDate() {
        // given
        final RentalSpecStatuesPerPropertyNumber statuses1 = new RentalSpecStatuesPerPropertyNumber("11111111", List.of(RentalSpecStatus.RETURNED, RentalSpecStatus.RETURNED, RentalSpecStatus.LOST));
        final RentalSpecStatuesPerPropertyNumber statuses2 = new RentalSpecStatuesPerPropertyNumber("22222222", List.of(RentalSpecStatus.BROKEN, RentalSpecStatus.LOST));
        given(rentalSpecRepository.findStatusesByPropertyNumbersBetweenDate(anySet(), any(), any()))
                .willReturn(List.of(statuses1, statuses2));

        // when
        final LocalDate now = LocalDate.now();
        final Map<String, RentalCountsDto> rentalCountsByPropertyNumbers =
                rentedItemService.getRentalCountsByPropertyNumbersBetweenDate(Set.of(statuses1.getPropertyNumber(), statuses2.getPropertyNumber()), now.minusDays(1), now);

        // then
        assertAll(
                () -> assertThat(rentalCountsByPropertyNumbers.get(statuses1.getPropertyNumber())).usingRecursiveComparison().isEqualTo(new RentalCountsDto(statuses1.getPropertyNumber(), 2, 1)),
                () -> assertThat(rentalCountsByPropertyNumbers.get(statuses2.getPropertyNumber())).usingRecursiveComparison().isEqualTo(new RentalCountsDto(statuses2.getPropertyNumber(), 0, 2))
        );
    }
}