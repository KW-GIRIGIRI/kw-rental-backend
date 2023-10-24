package com.girigiri.kwrental.asset.labroom.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;

public interface LabRoomDailyBanRepository extends Repository<LabRoomDailyBan, Long>, LabRoomDailyBanRepositoryCustom {
	List<LabRoomDailyBan> findByLabRoomId(Long labRoomId);

	void deleteById(Long id);

	Optional<LabRoomDailyBan> findByLabRoomIdAndBanDate(Long labRoomId, LocalDate date);

	LabRoomDailyBan save(LabRoomDailyBan labRoomDailyBan);
}
