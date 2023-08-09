package com.girigiri.kwrental.asset.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.asset.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.asset.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentViewServiceTest {

	@Mock
	private AssetService assetService;
	@Mock
	private EquipmentRetriever equipmentRetriever;
	@Mock
	private EquipmentRepository equipmentRepository;
	@Mock
	private RemainingQuantityService remainingQuantityService;
	@InjectMocks
	private EquipmentViewService equipmentViewService;

	@Test
	@DisplayName("등록된 기자재를 조회할 수 있다.")
	void findById() {
		// given
		final Equipment equipment = EquipmentFixture.create();
		final long id = 1L;
		given(equipmentRetriever.getEquipment(id)).willReturn(equipment);

		// when
		final EquipmentDetailResponse response = equipmentViewService.findById(id);

		// then
		assertThat(response).usingRecursiveComparison().ignoringFields("id")
			.isEqualTo(EquipmentDetailResponse.from(equipment));
	}

	@Test
	@DisplayName("특정 기자재의 날짜별 대여 가능 갯수 조회")
	void getRemainQuantitiesPerDate() {
		// given
		final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).build();
		final LocalDate now = LocalDate.now();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);
		final Map<LocalDate, Integer> reservedAmounts = Map.of(now, 10, now.plusDays(1), 5);
		given(remainingQuantityService.getReservedAmountInclusive(any(), any(), any()))
			.willReturn(reservedAmounts);
		given(assetService.getReservableCountPerDate(reservedAmounts, equipment))
			.willReturn(new RemainQuantitiesPerDateResponse(List.of(new RemainQuantityPerDateResponse(now, 0),
				new RemainQuantityPerDateResponse(now.plusDays(1), 5))));

		// when
		final RemainQuantitiesPerDateResponse actual = equipmentViewService.getRemainQuantitiesPerDate(1L, now,
			now.plusDays(1));

		// then
		assertThat(actual.getRemainQuantities()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new RemainQuantityPerDateResponse(now, 0),
				new RemainQuantityPerDateResponse(now.plusDays(1), 5));
	}

	@Test
	@DisplayName("등록된 기자재들을 대여 가능 횟수를 포함해서 페이지로 조건 없이 조회할 수 있다.")
	void findEquipmentsWithRentalQuantityBy() {
		// given
		final PageRequest pageable = PageRequest.of(1, 1, Sort.by("id").descending());

		final Equipment equipment = EquipmentFixture.builder().id(2L).build();
		given(equipmentRepository.findEquipmentBy(any(), any(), any()))
			.willReturn(new PageImpl<>(List.of(equipment), pageable, 3));
		given(remainingQuantityService.getRemainingQuantityByAssetIdAndDate(any(), any()))
			.willReturn(Map.of(equipment.getId(), equipment.getTotalQuantity()));

		// when
		final Page<SimpleEquipmentWithRentalQuantityResponse> expect = equipmentViewService.findEquipmentsWithRentalQuantityBy(
			pageable, new EquipmentSearchCondition(null, null, null));

		// then
		assertAll(
			() -> assertThat(expect.hasNext()).isTrue(),
			() -> assertThat(expect.hasPrevious()).isTrue(),
			() -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
				.containsExactly(
					SimpleEquipmentWithRentalQuantityResponse.from(equipment, equipment.getTotalQuantity()))
		);
	}

	@Test
	@DisplayName("등록된 기자재들을 페이지로 조건 없이 조회할 수 있다.")
	void findEquipmentsBy() {
		// given
		final PageRequest pageable = PageRequest.of(1, 1, Sort.by("id").descending());

		final Equipment equipment = EquipmentFixture.builder().id(2L).build();
		given(equipmentRepository.findEquipmentBy(any(), any(), any()))
			.willReturn(new PageImpl<>(List.of(equipment), pageable, 3));

		// when
		final Page<SimpleEquipmentResponse> expect = equipmentViewService.findEquipments(pageable,
			new EquipmentSearchCondition(null, null, null));

		// then
		assertAll(
			() -> assertThat(expect.hasNext()).isTrue(),
			() -> assertThat(expect.hasPrevious()).isTrue(),
			() -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
				.containsExactly(SimpleEquipmentResponse.from(equipment))
		);
	}
}