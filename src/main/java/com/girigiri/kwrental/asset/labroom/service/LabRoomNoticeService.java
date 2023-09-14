package com.girigiri.kwrental.asset.labroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LabRoomNoticeService {
	private final LabRoomRepository labRoomRepository;
	private final LabRoomRetriever labRoomRetriever;

	public void setNotice(String name, LabRoomNoticeRequest labRoomNoticeRequest) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		labRoomRepository.updateNotice(labRoom.getId(), labRoomNoticeRequest.getNotice());
	}

	@Transactional(readOnly = true)
	public LabRoomNoticeResponse getNotice(String name) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(name);
		return new LabRoomNoticeResponse(labRoom.getNotice());
	}
}
