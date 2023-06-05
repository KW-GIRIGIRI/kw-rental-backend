package com.girigiri.kwrental.labroom.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.labroom.domain.LabRoomDailyBan;

public interface LabRoomDailyBanRepository extends Repository<LabRoomDailyBan, Long> {
	void deleteById(Long id);

	Optional<LabRoomDailyBan> findByLabRoomIdAndBanDate(Long labRoomId, LocalDate date);

	LabRoomDailyBan save(LabRoomDailyBan labRoomDailyBan);
}
