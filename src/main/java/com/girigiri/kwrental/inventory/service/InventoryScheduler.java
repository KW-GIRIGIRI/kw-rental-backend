package com.girigiri.kwrental.inventory.service;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class InventoryScheduler {

	private final InventoryRepository inventoryRepository;

	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
	public void deletePastInventories() {
		final LocalDate date = RentalDateTime.now().toLocalDate();
		inventoryRepository.deleteRentalStartDateBeforeThan(date);
	}
}
