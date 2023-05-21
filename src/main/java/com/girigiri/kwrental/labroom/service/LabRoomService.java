package com.girigiri.kwrental.labroom.service;

import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.equipment.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.exception.LabRoomNotFoundException;
import com.girigiri.kwrental.labroom.repository.LabRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class LabRoomService {
    private final RemainingQuantityService remainingQuantityService;
    private final LabRoomRepository labRoomRepository;
    private final AssetService assetService;

    public LabRoomService(final RemainingQuantityService remainingQuantityService, final LabRoomRepository labRoomRepository, final AssetService assetService) {
        this.remainingQuantityService = remainingQuantityService;
        this.labRoomRepository = labRoomRepository;
        this.assetService = assetService;
    }

    @Transactional(readOnly = true)
    public RemainQuantitiesPerDateResponse getRemainQuantityByName(final String name, final LocalDate from, final LocalDate to) {
        final LabRoom labRoom = labRoomRepository.findLabRoomByName(name)
                .orElseThrow(LabRoomNotFoundException::new);
        final Map<LocalDate, Integer> reservedAmounts = remainingQuantityService.getReservedAmountBetween(labRoom.getId(), from, to);
        return assetService.getReservableCountPerDate(reservedAmounts, labRoom);
    }
}
