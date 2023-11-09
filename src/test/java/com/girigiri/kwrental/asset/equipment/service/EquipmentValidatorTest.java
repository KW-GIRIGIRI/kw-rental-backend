package com.girigiri.kwrental.asset.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.exception.DuplicateAssetNameException;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentValidatorTest {

	@Mock
	private EquipmentRetriever equipmentRetriever;
	@Mock
	private EquipmentRepository equipmentRepository;
	@InjectMocks
	private EquipmentValidator equipmentValidator;

	@Test
	@DisplayName("최대 대여일보다 긴 기간은 예외가 발생")
	void validateRentalDays_invalid() {
		// given
		final Equipment equipment = EquipmentFixture.builder().maxRentalDays(1).build();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);

		// when, then
		assertThatThrownBy(() -> equipmentValidator.validateRentalDays(1L, 2))
			.isExactlyInstanceOf(EquipmentException.class);
	}

	@Test
	@DisplayName("최대 대여일보다 짧거나 같은 기간은 검증을 통과")
	void validateRentalDays() {
		// given
		final Equipment equipment = EquipmentFixture.builder().maxRentalDays(1).build();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);

		// when, then
		assertThatCode(() -> equipmentValidator.validateRentalDays(1L, 1))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("이미 해당 이름으로 기자재가 존재하면 예외가 발생")
	void validateNotExistsByName() {
		// give
		final Equipment equipment = EquipmentFixture.create();
		given(equipmentRepository.findByName("name")).willReturn(List.of(equipment));

		// when, then
		assertThatThrownBy(() -> equipmentValidator.validateNotExistsByName("name"))
			.isExactlyInstanceOf(DuplicateAssetNameException.class);
	}

	@Test
	@DisplayName("이미 해당 이름으로 기자재가 존재했었지만 삭제가 된 경우 통과")
	void validateNotExistsByName_deleted() {
		// given
		final Equipment equipment = EquipmentFixture.create();
		equipment.delete();
		given(equipmentRepository.findByName("name")).willReturn(List.of(equipment));

		// when
		assertThatCode(() -> equipmentValidator.validateNotExistsByName("name"))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("해당 이름으로 기자재가 존재하지 않으면 통과")
	void validateNotExistsByName_notExists() {
		// given
		given(equipmentRepository.findByName("name")).willReturn(Collections.emptyList());

		// when
		assertThatCode(() -> equipmentValidator.validateNotExistsByName("name"))
			.doesNotThrowAnyException();
	}
}