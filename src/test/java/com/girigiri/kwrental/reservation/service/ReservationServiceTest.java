package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private RemainingQuantityServiceImpl remainingQuantityService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RentalSpecRepository rentalSpecRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("대여 예약을 생성한다.")
    void reserve() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final Inventory inventory = InventoryFixture.create(equipment);
        given(inventoryService.getInventoriesWithEquipment()).willReturn(List.of(inventory));
        doNothing().when(remainingQuantityService).validateAmount(any(), any(), any());

        final AddReservationRequest addReservationRequest = AddReservationRequest.builder()
                .renterEmail("email@email.com")
                .rentalPurpose("purpose")
                .renterPhoneNumber("01012341234")
                .renterName("name").build();
        final Reservation reservation = Reservation.builder()
                .id(1L)
                .phoneNumber(addReservationRequest.getRenterPhoneNumber())
                .purpose(addReservationRequest.getRentalPurpose())
                .email(addReservationRequest.getRenterEmail())
                .name(addReservationRequest.getRenterName())
                .reservationSpecs(List.of(ReservationSpecFixture.create(equipment)))
                .build();
        given(reservationRepository.save(any())).willReturn(reservation);

        // when
        final Long expect = reservationService.reserve(addReservationRequest);

        // then
        assertThat(expect).isOne();
    }

    @Test
    @DisplayName("특정 기간에 수령하는 특정 기자재의 대여 예약을 조회한다.")
    void getReservationsByEquipmentsPerYearMonth() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).build();
        final Reservation reservation = ReservationFixture.create(List.of(reservationSpec));
        given(rentalSpecRepository.findByStartDateBetween(any(), any(), any()))
                .willReturn(List.of(reservationSpec));

        // when
        final ReservationsByEquipmentPerYearMonthResponse expect = reservationService.getReservationsByEquipmentsPerYearMonth(equipment.getId(), YearMonth.now());

        // then
        assertThat(expect.getReservations().get(reservationSpec.getStartDate().getDayOfMonth())).containsExactlyInAnyOrder(reservation.getName());
    }
}