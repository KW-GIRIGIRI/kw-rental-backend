package com.girigiri.kwrental.rental.service.rent.creator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest.EquipmentRentalSpecsRequest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentRentalSpecCreatorTest {

	@InjectMocks
	private EquipmentRentalSpecCreator equipmentRentalSpecCreator;

	@Test
	@DisplayName("기자재 대여 상세를 생성한다.")
	void create() {
		// given
		final EquipmentRentalSpecsRequest equipmentRentalSpecsRequest = EquipmentRentalSpecsRequest.builder()
			.reservationSpecId(2L)
			.propertyNumbers(List.of("12345678")).build();
		final CreateEquipmentRentalRequest createEquipmentRentalRequest = CreateEquipmentRentalRequest.builder()
			.reservationId(1L)
			.equipmentRentalSpecsRequests(List.of(equipmentRentalSpecsRequest))
			.build();
		final EquipmentRentalSpec expect = EquipmentRentalSpecFixture.builder()
			.reservationId(createEquipmentRentalRequest.reservationId())
			.reservationSpecId(equipmentRentalSpecsRequest.reservationSpecId())
			.propertyNumber(equipmentRentalSpecsRequest.propertyNumbers().iterator().next())
			.build();

		// when
		final List<AbstractRentalSpec> actual = equipmentRentalSpecCreator.create(
			createEquipmentRentalRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptDateTime")
			.contains(expect);
	}
}