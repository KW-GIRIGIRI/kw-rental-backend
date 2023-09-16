package com.girigiri.kwrental.operation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.operation.domain.EntireOperation;
import com.girigiri.kwrental.operation.dto.response.EntireOperationResponse;
import com.girigiri.kwrental.operation.repository.EntireOperationRepository;

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

	@Transactional(readOnly = true)
	public EntireOperationResponse getEntireOperation() {
		final boolean isRunning = entireOperationRepository.findAll()
			.stream().map(EntireOperation::isRunning).findFirst().orElse(false);
		return new EntireOperationResponse(isRunning);
	}
}
