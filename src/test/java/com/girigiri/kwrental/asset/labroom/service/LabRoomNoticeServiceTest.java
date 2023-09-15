package com.girigiri.kwrental.asset.labroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomNoticeServiceTest {

	@Mock
	private LabRoomRepository labRoomRepository;
	@Mock
	private LabRoomRetriever labRoomRetriever;
	@InjectMocks
	private LabRoomNoticeService labRoomNoticeService;

	@Test
	@DisplayName("안내사항을 등록한다.")
	void setNotice() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);
		doNothing().when(labRoomRepository).updateNotice(hwado.getId(), "updated notice");

		// when
		labRoomNoticeService.setNotice("hwado", new LabRoomNoticeRequest("updated notice"));

		// then
		verify(labRoomRepository).updateNotice(hwado.getId(), "updated notice");
	}

	@Test
	@DisplayName("안내사항을 조회한다.")
	void getNotice() {
		// given
		LabRoom hwado = LabRoomFixture.builder().name("hwado").reservationCountPerDay(1).build();
		given(labRoomRetriever.getLabRoomByName("hwado")).willReturn(hwado);

		// when
		final LabRoomNoticeResponse actual = labRoomNoticeService.getNotice("hwado");

		// then
		assertThat(actual).isEqualTo(new LabRoomNoticeResponse(hwado.getNotice()));
	}
}