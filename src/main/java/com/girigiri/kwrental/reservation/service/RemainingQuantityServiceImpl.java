package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.equipment.service.RemainingQuantityService;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.AmountValidator;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.dto.ReservedAmount;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class RemainingQuantityServiceImpl implements RemainingQuantityService, AmountValidator {

    private final RentalSpecRepository rentalSpecRepository;
    private final EquipmentRepository equipmentRepository;

    public RemainingQuantityServiceImpl(final RentalSpecRepository rentalSpecRepository, final EquipmentRepository equipmentRepository) {
        this.rentalSpecRepository = rentalSpecRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Map<Long, Integer> getRemainingQuantityByEquipmentIdAndDate(final List<Long> equipmentIds, final LocalDate date) {
        return rentalSpecRepository.findRentalAmountsByEquipmentIds(equipmentIds, date)
                .stream()
                .collect(toMap(ReservedAmount::getEquipmentId, ReservedAmount::getRemainingAmount));
    }

    @Override   // TODO: 2023/04/23 반복문을 두번 도는 로직을 최적화 할 수 있다.
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public void validateAmount(final Long equipmentId, final Integer amount, final RentalPeriod rentalPeriod) {
        final Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(EquipmentNotFoundException::new);
        final List<RentalSpec> overlappedRentalSpecs = rentalSpecRepository.findOverlappedByPeriod(equipmentId, rentalPeriod);
        for (LocalDate i = rentalPeriod.getRentalStartDate(); i.isBefore(rentalPeriod.getRentalEndDate()); i = i.plusDays(1)) {
            final int rentedAmountByDate = sumRentedAmountByDate(overlappedRentalSpecs, i);
            validateTotalAmount(amount + rentedAmountByDate, equipment);
        }
    }

    private void validateTotalAmount(final Integer amount, final Equipment equipment) {
        if (amount > equipment.getTotalQuantity()) {
            throw new NotEnoughAmountException();
        }
    }

    private int sumRentedAmountByDate(final List<RentalSpec> overlappedRentalSpecs, final LocalDate date) {
        return overlappedRentalSpecs.stream()
                .filter(rentalSpec -> rentalSpec.containsDate(date))
                .mapToInt(rentalSpec -> rentalSpec.getAmount().getAmount())
                .sum();
    }
}
