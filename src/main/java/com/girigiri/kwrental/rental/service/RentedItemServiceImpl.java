package com.girigiri.kwrental.rental.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.service.RentedItemService;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.response.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.rental.exception.RentalSpecRentedWhenRemoveAssetException;
import com.girigiri.kwrental.rental.exception.RentalSpecRentedWhenRemoveItemException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;

@Service
public class RentedItemServiceImpl implements RentedItemService {

	private final RentalSpecRepository rentalSpecRepository;

	public RentedItemServiceImpl(final RentalSpecRepository rentalSpecRepository) {
		this.rentalSpecRepository = rentalSpecRepository;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<String> getRentedPropertyNumbers(final Long equipmentId, final LocalDateTime dateTime) {
		return rentalSpecRepository.findRentedRentalSpecsByAssetId(equipmentId, dateTime)
			.stream()
			.map(EquipmentRentalSpec::getPropertyNumber)
			.collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<String, RentalCountsDto> getRentalCountsByPropertyNumbersBetweenDate(final Set<String> propertyNumbers,
		final LocalDate from, final LocalDate to) {
		return rentalSpecRepository.findStatusesByPropertyNumbersBetweenDate(propertyNumbers, from, to)
			.stream()
			.collect(
				Collectors.toMap(RentalSpecStatuesPerPropertyNumber::propertyNumber, this::mapToRentalCountsDto));
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int updatePropertyNumbers(final List<ToBeUpdatedItem> toBeUpdatedItems) {
		return rentalSpecRepository.updatePropertyNumbers(toBeUpdatedItems);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateNotRentedByAssetId(Long assetId) {
		boolean anyRented = !rentalSpecRepository.findRentedRentalSpecsByAssetId(assetId).isEmpty();
		if (anyRented) {
			throw new RentalSpecRentedWhenRemoveAssetException();
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateNotRentedByPropertyNumbers(final Collection<String> propertyNumbers) {
		final boolean anyRented = !rentalSpecRepository.findRentedRentalSpecsByPropertyNumberIn(propertyNumbers)
			.isEmpty();
		if (anyRented)
			throw new RentalSpecRentedWhenRemoveItemException();
	}

	private RentalCountsDto mapToRentalCountsDto(final RentalSpecStatuesPerPropertyNumber rentalSpecStatues) {
		return new RentalCountsDto(rentalSpecStatues.propertyNumber(), rentalSpecStatues.getNormalReturnedCount(),
			rentalSpecStatues.getAbnormalReturnedCount());
	}
}
