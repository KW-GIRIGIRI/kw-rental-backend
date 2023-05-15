package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.service.RentedItemService;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RentedItemServiceImpl implements RentedItemService {

    private final RentalSpecRepository rentalSpecRepository;

    public RentedItemServiceImpl(final RentalSpecRepository rentalSpecRepository) {
        this.rentalSpecRepository = rentalSpecRepository;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<String> getRentedPropertyNumbers(final Long equipmentId, final LocalDateTime dateTime) {
        return rentalSpecRepository.findRentedRentalSpecs(equipmentId, dateTime)
                .stream()
                .map(RentalSpec::getPropertyNumber)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Map<String, RentalCountsDto> getRentalCountsByPropertyNumbersBetweenDate(final Set<String> propertyNumbers, final LocalDate from, final LocalDate to) {
        return rentalSpecRepository.findStatusesByPropertyNumbersBetweenDate(propertyNumbers, from, to)
                .stream()
                .collect(Collectors.toMap(RentalSpecStatuesPerPropertyNumber::getPropertyNumber, this::mapToRentalCountsDto));
    }

    private RentalCountsDto mapToRentalCountsDto(final RentalSpecStatuesPerPropertyNumber rentalSpecStatues) {
        return new RentalCountsDto(
                rentalSpecStatues.getPropertyNumber(),
                rentalSpecStatues.getNormalReturnedCount(),
                rentalSpecStatues.getAbnormalReturnedCount()
        );
    }
}
