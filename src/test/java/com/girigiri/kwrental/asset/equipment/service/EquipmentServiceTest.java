package com.girigiri.kwrental.asset.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.asset.equipment.exception.DuplicateAssetNameException;
import com.girigiri.kwrental.asset.equipment.exception.InvalidCategoryException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

	@Mock
	private EquipmentRepository equipmentRepository;
	@Mock
	private EquipmentRetriever equipmentRetriever;
	@Mock
	private ItemSaverPerEquipment itemSaverPerEquipment;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Mock
	private EquipmentValidator equipmentValidator;
	@InjectMocks
	private EquipmentService equipmentService;

	@Test
	@DisplayName("기자재 저장 API")
	void saveEquipment() {
		// given
		AddEquipmentRequest addEquipmentRequest = new AddEquipmentRequest("rentalPlace", "modelName", "CAMERA",
			"maker", "imgUrl", "component", "purpose", "description", 1, 2);
		final AddItemRequest addItemRequest = new AddItemRequest("propertyNumber");
		final AddEquipmentWithItemsRequest request = new AddEquipmentWithItemsRequest(addEquipmentRequest,
			List.of(addItemRequest));
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		given(equipmentRepository.save(any())).willReturn(equipment);

		// when
		final Long id = equipmentService.saveEquipment(request);

		// then
		assertThat(id).isOne();
		verify(equipmentRepository).save(any());
		verify(itemSaverPerEquipment).execute(List.of(new ToBeSavedItem(addItemRequest.propertyNumber(), equipment.getId())));
	}

	@Test
	@DisplayName("기자재 저장에서 잘못된 카테고리 예외")
	void saveEquipment_invalidCategory() {
		// given
		AddEquipmentRequest addEquipmentRequest = new AddEquipmentRequest(
			"rentalPlace", "modelName", "invalidCategory",
			"maker", "imgUrl", "component",
			"purpose", "description", 1, 1);
		final AddItemRequest addItemRequest = new AddItemRequest("propertyNumber");
		final AddEquipmentWithItemsRequest request = new AddEquipmentWithItemsRequest(addEquipmentRequest,
			List.of(addItemRequest));

		// when, then
		assertThatThrownBy(() -> equipmentService.saveEquipment(request))
			.isExactlyInstanceOf(InvalidCategoryException.class);
	}

	@Test
	@DisplayName("이미 같은 이름의 이름가진 기자재가 존재하면 예외")
	void saveEquipment_existsName() {
		// given
		AddEquipmentRequest addEquipmentRequest = new AddEquipmentRequest(
			"rentalPlace", "modelName", "invalidCategory",
			"maker", "imgUrl", "component",
			"purpose", "description", 1, 1);
		final AddItemRequest addItemRequest = new AddItemRequest("propertyNumber");
		final AddEquipmentWithItemsRequest request = new AddEquipmentWithItemsRequest(addEquipmentRequest,
			List.of(addItemRequest));
		doThrow(DuplicateAssetNameException.class).when(equipmentValidator)
			.validateNotExistsByName(addEquipmentRequest.modelName());

		// when, then
		assertThatThrownBy(() -> equipmentService.saveEquipment(request))
			.isExactlyInstanceOf(DuplicateAssetNameException.class);
	}

	@Test
	@DisplayName("기자재 삭제")
	void deleteEquipment() {
		// given
		Equipment equipment = EquipmentFixture.create();
		given(equipmentRetriever.getEquipment(1L)).willReturn(equipment);

		// when
		equipmentService.deleteEquipment(1L);

		// then
		verify(eventPublisher).publishEvent(any(EquipmentDeleteEvent.class));
		assertThat(equipment.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("기자재 수정 ")
	void updateEquipment() {
		// given
		Equipment equipment = EquipmentFixture.create();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);
		UpdateEquipmentRequest updateEquipmentRequest = new UpdateEquipmentRequest(
			"updatedDays", "updatedName",
			"ETC", "updatedMaker", "updatedImgUrl",
			"updatedComponent", "updatedPurpose", "updatedDescription", 2, 2);

		// when
		EquipmentDetailResponse expect = equipmentService.update(1L, updateEquipmentRequest);

		// then
		assertThat(expect).usingRecursiveComparison()
			.isEqualTo(EquipmentDetailResponse.from(equipment));
	}
}
