package com.girigiri.kwrental.labroom.repository;

import com.girigiri.kwrental.labroom.domain.LabRoom;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface LabRoomRepository extends Repository<LabRoom, Long>, LabRoomRepositoryCustom {
    Optional<LabRoom> findLabRoomByName(String name);

    LabRoom save(LabRoom labRoom);
}
