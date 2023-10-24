package com.girigiri.kwrental.asset.labroom.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;

public interface LabRoomRepository extends Repository<LabRoom, Long>, LabRoomRepositoryCustom {
	Optional<LabRoom> findLabRoomByName(String name);

	LabRoom save(LabRoom labRoom);
}
