package com.girigiri.kwrental.asset.labroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotFoundException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomRetrieverTest {

	@Mock
	private LabRoomRepository labRoomRepository;
	@InjectMocks
	private LabRoomRetriever labRoomRetriever;

	@Test
	@DisplayName("이름으로 랩실을 조회한다.")
	void getLabRoomByName() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRepository.findLabRoomByName("hwado")).willReturn(Optional.of(hwado));

		// when
		final LabRoom actual = labRoomRetriever.getLabRoomByName("hwado");

		// then
		assertThat(actual).isEqualTo(hwado);
	}

	@Test
	@DisplayName("이름으로 랩실을 조회할 때 없으면 예외가 발생한다.")
	void getLabRoomByName_notExists() {
		// given
		given(labRoomRepository.findLabRoomByName("hwado")).willReturn(Optional.empty());

		// when, then
		assertThatCode(() -> labRoomRetriever.getLabRoomByName("hwado"))
			.isExactlyInstanceOf(LabRoomNotFoundException.class);
	}
}