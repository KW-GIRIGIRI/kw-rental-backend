package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.equipment.service.RemainingQuantityService;
import com.girigiri.kwrental.reservation.dto.ReservedAmount;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class RemainingQuantityServiceImpl implements RemainingQuantityService {

    private final RentalSpecRepository rentalSpecRepository;

    public RemainingQuantityServiceImpl(final RentalSpecRepository rentalSpecRepository) {
        this.rentalSpecRepository = rentalSpecRepository;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Map<Long, Integer> getRemainingQuantityByEquipmentIdAndDate(final List<Long> equipmentIds, final LocalDate date) {
        return rentalSpecRepository.findRentalAmountsByEquipmentIds(equipmentIds, date)
                .stream()
                .collect(toMap(ReservedAmount::getEquipmentId, ReservedAmount::getRemainingAmount));
    }
}
