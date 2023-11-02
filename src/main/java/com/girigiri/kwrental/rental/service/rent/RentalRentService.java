package com.girigiri.kwrental.rental.service.rent;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.service.rent.acceptor.EquipmentReservationAcceptor;
import com.girigiri.kwrental.rental.service.rent.acceptor.LabRoomReservationAcceptor;
import com.girigiri.kwrental.rental.service.rent.acceptor.ReservationAcceptor;
import com.girigiri.kwrental.rental.service.rent.creator.EquipmentRentalSpecCreator;
import com.girigiri.kwrental.rental.service.rent.creator.LabRoomRentalSpecCreator;
import com.girigiri.kwrental.rental.service.rent.creator.RentalSpecCreator;
import com.girigiri.kwrental.rental.service.rent.validator.EquipmentRentValidator;
import com.girigiri.kwrental.rental.service.rent.validator.LabRoomRentValidator;
import com.girigiri.kwrental.rental.service.rent.validator.RentValidator;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RentalRentService {
	private final RentalSpecRepository rentalSpecRepository;
	private final EquipmentRentValidator equipmentRentValidator;
	private final EquipmentRentalSpecCreator equipmentRentalSpecCreator;
	private final EquipmentReservationAcceptor equipmentReservationAcceptor;
	private final LabRoomRentValidator labRoomRentValidator;
	private final LabRoomRentalSpecCreator labRoomRentalSpecCreator;
	private final LabRoomReservationAcceptor labRoomReservationAcceptor;

	public void rentEquipment(final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		log.info("[DEBUGGING] property numbers is null? {}", Objects.isNull(createEquipmentRentalRequest.rentalSpecsRequests()));
		rent(createEquipmentRentalRequest, equipmentRentValidator, equipmentRentalSpecCreator,
			equipmentReservationAcceptor);
	}

	public void rentLabRoom(final CreateLabRoomRentalRequest createLabRoomRentalRequest) {
		rent(createLabRoomRentalRequest, labRoomRentValidator, labRoomRentalSpecCreator, labRoomReservationAcceptor);
	}

	private <T> void rent(final T request, final RentValidator<T> rentValidator, final RentalSpecCreator<T> creator,
		final ReservationAcceptor acceptor) {
		rentValidator.validate(request);
		final List<RentalSpec> rentalSpecs = creator.create(request);
		rentalSpecRepository.saveAll(rentalSpecs);
		acceptor.acceptReservationsBy(rentalSpecs);
	}
}
