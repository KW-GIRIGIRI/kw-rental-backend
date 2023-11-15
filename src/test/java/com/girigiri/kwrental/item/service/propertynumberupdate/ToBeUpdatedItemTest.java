package com.girigiri.kwrental.item.service.propertynumberupdate;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.item.exception.ItemException;

class ToBeUpdatedItemTest {

	@Test
	@DisplayName("자산번호를 null로 수정 시 예외")
	void updatePropertyNumber_Null() {
		// given, when, then
		assertThatThrownBy(() -> new ToBeUpdatedItem(1L, 1L, "123456789", null))
			.isInstanceOf(ItemException.class);
	}

	@Test
	@DisplayName("자산번호를 빈 값으로 수정 시 예외")
	void updatePropertyNumber_empty() {
		// given, when, then
		assertThatThrownBy(() -> new ToBeUpdatedItem(1L, 1L, "123456789", ""))
			.isInstanceOf(ItemException.class);
	}

	@Test
	@DisplayName("자산번호를 공백으로 수정 시 예외")
	void updatePropertyNumber_blank() {
		// given, when, then
		assertThatThrownBy(() -> new ToBeUpdatedItem(1L, 1L, "123456789", "    "))
			.isInstanceOf(ItemException.class);
	}

	@Test
	@DisplayName("기존의 자산번호와 바꾸려는 자산번호가 같으면 안됩니다.")
	void propertyNumberIsSame() {
		// given, when, then
		assertThatThrownBy(() -> new ToBeUpdatedItem(1L, 1L, "11111111", "11111111"))
			.isExactlyInstanceOf(ItemException.class);
	}
}