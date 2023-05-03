package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private ItemService itemService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private RentalSpecRepository rentalSpecRepository;

    @InjectMocks
    private RentalService rentalService;


    @Test
    @DisplayName("대여를 생성할 때 이미 대여 중인 품목으로 대여하려면 예외 발생")
    void rent_duplicateRental() {
        // given
        final Long reservationId = 1L;
        final String propertyNumber = "12345678";
        final Long reservationSpecId = 2L;
        final CreateRentalRequest request = new CreateRentalRequest(
                reservationId, List.of(new RentalSpecsRequest(reservationSpecId, List.of(propertyNumber)))
        );
        given(reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(any(), any()))
                .willReturn(Map.of(1L, Set.of(propertyNumber)));
        doNothing().when(itemService).validatePropertyNumbers(any());
        given(rentalSpecRepository.findByPropertyNumbers(any()))
                .willReturn(List.of(RentalSpecFixture.builder().id(1L).reservationSpecId(reservationSpecId).build()));

        // when, then
        assertThatThrownBy(() -> rentalService.rent(request))
                .isExactlyInstanceOf(DuplicateRentalException.class);
    }

    @Test
    @DisplayName("대여를 생성한다.")
    void rent() {
        // given
        final Long reservationId = 1L;
        final String propertyNumber = "12345678";
        final Long reservationSpecId = 2L;
        final CreateRentalRequest request = new CreateRentalRequest(
                reservationId, List.of(new RentalSpecsRequest(reservationSpecId, List.of(propertyNumber)))
        );
        given(reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(any(), any()))
                .willReturn(Map.of(1L, Set.of(propertyNumber)));
        doNothing().when(itemService).validatePropertyNumbers(any());
        given(rentalSpecRepository.findByPropertyNumbers(any()))
                .willReturn(Collections.emptyList());
        final List<RentalSpec> rentalSpecs = List.of(RentalSpecFixture.builder().id(1L).reservationSpecId(reservationSpecId).build());
        given(rentalSpecRepository.saveAll(any()))
                .willReturn(rentalSpecs);

        // when, then
        assertThatCode(() -> rentalService.rent(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 날짜가 대여 수령일인 대여 예약을 대여 수령 시간과 대여 상세를 함께 조회한다.")
    void getReservationsWithRentalSpecsByStartDate() {
        // given
        final Equipment equipment1 = EquipmentFixture.create();
        final Equipment equipment2 = EquipmentFixture.create();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).id(1L).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).id(2L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).id(1L).acceptDateTime(LocalDateTime.now()).build();
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().reservationSpecId(reservationSpec2.getId()).build();
        given(reservationService.getReservationsByStartDate(any())).willReturn(List.of(reservation));
        given(rentalSpecRepository.findByReservationSpecIds(Set.of(reservationSpec1.getId(), reservationSpec2.getId()))).willReturn(List.of(rentalSpec1, rentalSpec2));

        // when
        final ReservationsWithRentalSpecsResponse response = rentalService.getReservationsWithRentalSpecsByStartDate(LocalDate.now());

        assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ReservationWithRentalSpecsResponse.of(reservation, List.of(rentalSpec1, rentalSpec2)));
    }
}