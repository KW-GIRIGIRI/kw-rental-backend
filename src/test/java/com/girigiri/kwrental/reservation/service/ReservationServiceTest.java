package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private ReservationSpecRepository reservationSpecRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("대여 예약을 생성한다.")
    void reserve() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final Inventory inventory = InventoryFixture.create(equipment, 0L);
        given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory));
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
        final Long expect = reservationService.reserve(1L, addReservationRequest);

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
        given(reservationSpecRepository.findByStartDateBetween(any(), any(), any()))
                .willReturn(List.of(reservationSpec));

        // when
        final ReservationsByEquipmentPerYearMonthResponse expect = reservationService.getReservationsByEquipmentsPerYearMonth(equipment.getId(), YearMonth.now());

        // then
        assertThat(expect.getReservations().get(reservationSpec.getStartDate().getDayOfMonth())).containsExactlyInAnyOrder(reservation.getName());
    }

    @Test
    @DisplayName("특정 날짜에 수령하는 대여 예약을 조회한다.")
    void getReservationsWithMemberNumberByStartDate() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).build();
        final Reservation reservation = ReservationFixture.create(List.of(reservationSpec));
        final ReservationWithMemberNumber reservationWithMemberNumber = new ReservationWithMemberNumber(reservation, "11111111");
        given(reservationRepository.findReservationsWithSpecsByStartDate(any()))
                .willReturn(Set.of(reservationWithMemberNumber));

        // when
        final Set<ReservationWithMemberNumber> reservationsByStartDate = reservationService.getReservationsByStartDate(LocalDate.now());

        // then
        assertThat(reservationsByStartDate).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(reservationWithMemberNumber);
    }

    @Test
    @DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증하고 기자재 별로 다시 분류한다.")
    void validatePropertyNumbersCountAndGroupByEquipmentId() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).id(2L).amount(RentalAmount.ofPositive(2)).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
        given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

        // when
        final Map<Long, Set<String>> propertyNumbersByEquipmentId = reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(
                reservation.getId(), Map.of(reservationSpec.getId(), Set.of("1111111", "222222222"))
        );

        // then
        assertThat(propertyNumbersByEquipmentId.get(equipment.getId())).containsExactlyInAnyOrder("1111111", "222222222");
    }

    @Test
    @DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 예약 신청한 갯수와 일치하지 않으면 예외발생")
    void validatePropertyNumbersCountAndGroupByEquipmentId_notSameAmount() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).id(2L).amount(RentalAmount.ofPositive(1)).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
        given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

        // when, then
        assertThatThrownBy(() -> reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(
                reservation.getId(), Map.of(reservationSpec.getId(), Set.of("1111111", "222222222"))))
                .isExactlyInstanceOf(ReservationSpecException.class);
    }

    @Test
    @DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증하려는데 예약 상세가 존재하지 않으면 예외발생")
    void validatePropertyNumbersCountAndGroupByEquipmentId_notFoundReservationSpec() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).id(2L).amount(RentalAmount.ofPositive(1)).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
        given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

        // when, then
        assertThatThrownBy(() -> reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(
                reservation.getId(), Map.of(4L, Set.of("1111111", "222222222"), 2L, Set.of("333333333"))))
                .isExactlyInstanceOf(ReservationSpecException.class);
    }

    @Test
    @DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한 지 검증하려하는데 대여 예약이 존재하지 않으면 예외 발생")
    void validatePropertyNumbersCountAndGroupByEquipmentId_notFoundReservation() {
        // given
        given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(
                1L, Map.of(1L, Set.of("1111111", "222222222"))))
                .isExactlyInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않은 대여 예약 조회 시 예외 발생")
    void getReservationWithReservationSpecsById() {
        // given
        given(reservationRepository.findByIdWithSpecs(any()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> reservationService.getReservationWithReservationSpecsById(1L))
                .isExactlyInstanceOf(ReservationNotFoundException.class);
    }
}