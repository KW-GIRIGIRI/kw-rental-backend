package com.girigiri.kwrental.asset.labroom.repository;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;

public interface LabRoomDailyBanRepositoryCustom {
	List<LabRoomDailyBan> findByLabRoomIdAndInclusive(Long id, LocalDate from, LocalDate to);

}
