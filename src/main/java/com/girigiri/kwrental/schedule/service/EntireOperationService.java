package com.girigiri.kwrental.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.schedule.domain.EntireOperation;
import com.girigiri.kwrental.schedule.repository.EntireOperationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntireOperationService {
	private final EntireOperationRepository entireOperationRepository;

	@Transactional
	public void putEntireOperation(final boolean isRunning) {
		final boolean exists = entireOperationRepository.exists();
		if (exists) {
			entireOperationRepository.updateEntireOperation(isRunning);
		} else {
			entireOperationRepository.save(EntireOperation.builder().isRunning(isRunning).build());
		}
	}
}
