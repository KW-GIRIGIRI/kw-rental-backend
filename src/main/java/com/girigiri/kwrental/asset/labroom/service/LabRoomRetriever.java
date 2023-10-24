package com.girigiri.kwrental.asset.labroom.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotFoundException;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabRoomRetriever {

	private final LabRoomRepository labRoomRepository;

	public LabRoom getLabRoomByName(String name) {
		return labRoomRepository.findLabRoomByName(name)
			.orElseThrow(LabRoomNotFoundException::new);
	}
}
