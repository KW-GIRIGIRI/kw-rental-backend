package com.girigiri.kwrental.rental.service.rent.creator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentRentalSpecCreatorTest {

	@InjectMocks
	private EquipmentRentalSpecCreator equipmentRentalSpecCreator;

	@Test
	@DisplayName("기자재 대여 상세를 생성한다.")
	void create() {
		// given
		final RentalSpecsRequest rentalSpecsRequest = RentalSpecsRequest.builder()
			.reservationSpecId(2L)
			.propertyNumbers(List.of("12345678")).build();
		final CreateEquipmentRentalRequest createEquipmentRentalRequest = CreateEquipmentRentalRequest.builder()
			.reservationId(1L)
			.rentalSpecsRequests(List.of(rentalSpecsRequest))
			.build();
		final EquipmentRentalSpec expect = EquipmentRentalSpecFixture.builder()
			.reservationId(createEquipmentRentalRequest.getReservationId())
			.reservationSpecId(rentalSpecsRequest.getReservationSpecId())
			.propertyNumber(rentalSpecsRequest.getPropertyNumbers().iterator().next())
			.build();

		// when
		final List<AbstractRentalSpec> actual = equipmentRentalSpecCreator.create(
			createEquipmentRentalRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptDateTime")
			.contains(expect);
	}
}