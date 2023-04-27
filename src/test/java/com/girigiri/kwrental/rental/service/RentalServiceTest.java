package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.service.ItemServiceImpl;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private ItemServiceImpl itemService;
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
}