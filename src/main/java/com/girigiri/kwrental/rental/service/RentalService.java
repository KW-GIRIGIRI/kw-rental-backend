package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.Rental;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalSpecRequest;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.dto.response.RentalSpecsByItemResponse;
import com.girigiri.kwrental.rental.dto.response.RentalsDto;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class RentalService {

    private final ItemService itemService;
    private final ReservationService reservationService;
    private final RentalSpecRepository rentalSpecRepository;

    public RentalService(final ItemService itemService, final ReservationService reservationService, final RentalSpecRepository rentalSpecRepository) {
        this.itemService = itemService;
        this.reservationService = reservationService;
        this.rentalSpecRepository = rentalSpecRepository;
    }

    @Transactional
    public void rent(final CreateRentalRequest createRentalRequest) {
        final Map<Long, Set<String>> propertyNumbersByReservationSpecId = createRentalRequest.getRentalSpecsRequests().stream()
                .collect(toMap(RentalSpecsRequest::getReservationSpecId, it -> Set.copyOf(it.getPropertyNumbers())));
        Map<Long, Set<String>> collectedByEquipmentId = reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(createRentalRequest.getReservationId(), propertyNumbersByReservationSpecId);
        itemService.validatePropertyNumbers(collectedByEquipmentId);
        validateNowRental(collectedByEquipmentId);
        final List<EquipmentRentalSpec> rentalSpecs = mapToRentalSpecs(createRentalRequest);
        rentalSpecRepository.saveAll(rentalSpecs);
        reservationService.acceptReservation(createRentalRequest.getReservationId());
    }

    private void validateNowRental(final Map<Long, Set<String>> collectedByEquipmentId) {
        final Set<String> propertyNumbers = collectedByEquipmentId.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        final boolean nowRental = rentalSpecRepository.findByPropertyNumbers(propertyNumbers).stream()
                .anyMatch(EquipmentRentalSpec::isNowRental);
        if (nowRental) throw new DuplicateRentalException();
    }

    private List<EquipmentRentalSpec> mapToRentalSpecs(final CreateRentalRequest createRentalRequest) {
        return createRentalRequest.getRentalSpecsRequests().stream()
                .map(it -> mapToRentalSpecPerReservationSpec(createRentalRequest.getReservationId(), it))
                .flatMap(List::stream)
                .toList();
    }

    private List<EquipmentRentalSpec> mapToRentalSpecPerReservationSpec(final Long reservationId, final RentalSpecsRequest rentalSpecsRequest) {
        final Long reservationSpecId = rentalSpecsRequest.getReservationSpecId();
        return rentalSpecsRequest.getPropertyNumbers().stream()
                .map(propertyNumber -> mapToRentalSpec(reservationId, reservationSpecId, propertyNumber))
                .toList();
    }

    private EquipmentRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId, final String propertyNumber) {
        return EquipmentRentalSpec.builder()
                .reservationId(reservationId)
                .reservationSpecId(reservationSpecId)
                .propertyNumber(propertyNumber)
                .build();
    }

    @Transactional(readOnly = true)
    public EquipmentReservationsWithRentalSpecsResponse getReservationsWithRentalSpecsByStartDate(final LocalDate localDate) {
        final Set<EquipmentReservationWithMemberNumber> reservations = reservationService.getReservationsByStartDate(localDate);
        final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(reservations);
        final List<EquipmentRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
        return EquipmentReservationsWithRentalSpecsResponse.of(reservations, rentalSpecs);
    }

    private Set<Long> getAcceptedReservationSpecIds(final Set<EquipmentReservationWithMemberNumber> reservationsWithMemberNumber) {
        return reservationsWithMemberNumber.stream()
                .filter(EquipmentReservationWithMemberNumber::isAccepted)
                .map(EquipmentReservationWithMemberNumber::getReservationSpecIds)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public ReservationsWithRentalSpecsByEndDateResponse getReservationsWithRentalSpecsByEndDate(final LocalDate endDate) {
        final OverdueReservationsWithRentalSpecsResponse overdueReservations = getOverdueReservationsWithRentalSpecs(endDate);
        final EquipmentReservationsWithRentalSpecsResponse equipmentReservations = getReservationWithRentalSpecsByEndDate(endDate);
        return new ReservationsWithRentalSpecsByEndDateResponse(overdueReservations, equipmentReservations);
    }

    private OverdueReservationsWithRentalSpecsResponse getOverdueReservationsWithRentalSpecs(final LocalDate localDate) {
        Set<EquipmentReservationWithMemberNumber> overdueEquipmentReservations = reservationService.getOverdueReservationsWithMemberNumber(localDate);
        final Set<Long> overdueReservationSpecsIds = getAcceptedReservationSpecIds(overdueEquipmentReservations);
        final List<EquipmentRentalSpec> overdueRentalSpecs = rentalSpecRepository.findByReservationSpecIds(overdueReservationSpecsIds);
        return OverdueReservationsWithRentalSpecsResponse.of(overdueEquipmentReservations, overdueRentalSpecs);
    }

    private EquipmentReservationsWithRentalSpecsResponse getReservationWithRentalSpecsByEndDate(final LocalDate localDate) {
        Set<EquipmentReservationWithMemberNumber> equipmentReservations = reservationService.getReservationsWithMemberNumberByEndDate(localDate);
        final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(equipmentReservations);
        final List<EquipmentRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
        return EquipmentReservationsWithRentalSpecsResponse.of(equipmentReservations, rentalSpecs);
    }

    @Transactional
    public void returnRental(final ReturnRentalRequest returnRentalRequest) {
        Rental rental = getRental(returnRentalRequest);
        final Map<Long, RentalSpecStatus> returnRequest = returnRentalRequest.getRentalSpecs().stream()
                .collect(toMap(ReturnRentalSpecRequest::getId, ReturnRentalSpecRequest::getStatus));
        for (Long rentalSpecId : returnRequest.keySet()) {
            final RentalSpecStatus status = returnRequest.get(rentalSpecId);
            rental.returnByRentalSpecId(rentalSpecId, status);
            setPenaltyAndItemAvailable(rental.getRentalSpecAs(rentalSpecId, EquipmentRentalSpec.class));
        }
        rental.setReservationStatusAfterReturn();
    }

    private Rental getRental(final ReturnRentalRequest returnRentalRequest) {
        final List<EquipmentRentalSpec> rentalSpecList = rentalSpecRepository.findByReservationId(returnRentalRequest.getReservationId());
        final Reservation reservation = reservationService.getReservationWithReservationSpecsById(returnRentalRequest.getReservationId());
        return Rental.of(rentalSpecList, reservation);
    }

    private void setPenaltyAndItemAvailable(final EquipmentRentalSpec rentalSpec) {
        if (rentalSpec.isUnavailableAfterReturn()) {
            itemService.setAvailable(rentalSpec.getPropertyNumber(), false);
        }
        if (rentalSpec.isOverdueReturned()) {
            itemService.setAvailable(rentalSpec.getPropertyNumber(), true);
        }
    }

    @Transactional(readOnly = true)
    public RentalsDto getRentalsBetweenDate(final Long memberId, final LocalDate from, final LocalDate to) {
        final List<RentalDto> rentalDtosBetweenDate = rentalSpecRepository.findRentalDtosBetweenDate(memberId, from, to);
        return new RentalsDto(new LinkedHashSet<>(rentalDtosBetweenDate));
    }

    @Transactional(readOnly = true)
    public RentalSpecsByItemResponse getReturnedRentalSpecs(final String propertyNumber) {
        final List<RentalSpecWithName> rentalSpecsWithName = rentalSpecRepository.findTerminatedWithNameByPropertyNumber(propertyNumber);
        Collections.reverse(rentalSpecsWithName);
        return RentalSpecsByItemResponse.from(rentalSpecsWithName);
    }

    @Transactional
    public void rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
        final List<Reservation> rentedReservations = reservationService.rentLabRoom(rentLabRoomRequest);
        final List<LabRoomRentalSpec> labRoomRentalSpecs = rentedReservations.stream()
                .map(reservation -> mapToRentalSpec(reservation.getId(), reservation.getReservationSpecs().get(0).getId()))
                .toList();
        rentalSpecRepository.saveAll(labRoomRentalSpecs);
    }

    private LabRoomRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId) {
        return LabRoomRentalSpec.builder()
                .reservationId(reservationId)
                .reservationSpecId(reservationSpecId)
                .build();
    }

    @Transactional
    public void returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
        final List<Reservation> returnedReservations = reservationService.returnLabRoom(returnLabRoomRequest);
        final List<Long> reservationIds = returnedReservations.stream()
                .map(Reservation::getId)
                .toList();
        rentalSpecRepository.updateNormalReturnedByReservationIds(reservationIds, RentalDateTime.now());
    }
}
