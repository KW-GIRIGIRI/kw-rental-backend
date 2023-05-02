package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.service.RentedItemService;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RentedItemServiceImpl implements RentedItemService {

    private RentalSpecRepository rentalSpecRepository;

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
}
