package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RemainingQuantityServiceImplTest {

    @Mock
    private RentalSpecRepository rentalSpecRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private RemainingQuantityServiceImpl remainingQuantityService;

    @Test
    @DisplayName("대여 가능한 갯수를 확인")
    void validateAmount() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(2).build();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment).amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment).amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();

        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));
        given(rentalSpecRepository.findOverlappedByPeriod(any(), any())).willReturn(List.of(reservationSpec1, reservationSpec2));

        // when, then
        assertThatCode(() -> remainingQuantityService.validateAmount(1L, 1, new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("대여 가능한 갯수가 요청한 갯수보다 부족하면 예외 발생")
    void validateAmount_notEnoughRemainingAmount() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(2).build();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment).amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment).amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();

        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));
        given(rentalSpecRepository.findOverlappedByPeriod(any(), any())).willReturn(List.of(reservationSpec1, reservationSpec2));

        // when, then
        assertThatThrownBy(() -> remainingQuantityService.validateAmount(1L, 1, new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))))
                .isExactlyInstanceOf(NotEnoughAmountException.class);
    }
}