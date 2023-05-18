package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.Rental;
import com.girigiri.kwrental.rental.domain.RentalSpec;
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
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsAndMemberNumberResponse;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.Reservations;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
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
        final List<RentalSpec> rentalSpecs = mapToRentalSpecs(createRentalRequest);
        rentalSpecRepository.saveAll(rentalSpecs);
        reservationService.acceptReservation(createRentalRequest.getReservationId());
    }

    private void validateNowRental(final Map<Long, Set<String>> collectedByEquipmentId) {
        final Set<String> propertyNumbers = collectedByEquipmentId.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        final boolean nowRental = rentalSpecRepository.findByPropertyNumbers(propertyNumbers).stream()
                .anyMatch(RentalSpec::isNowRental);
        if (nowRental) throw new DuplicateRentalException();
    }

    private List<RentalSpec> mapToRentalSpecs(final CreateRentalRequest createRentalRequest) {
        return createRentalRequest.getRentalSpecsRequests().stream()
                .map(it -> mapToRentalSpecPerReservationSpec(createRentalRequest.getReservationId(), it))
                .flatMap(List::stream)
                .toList();
    }

    private List<RentalSpec> mapToRentalSpecPerReservationSpec(final Long reservationId, final RentalSpecsRequest rentalSpecsRequest) {
        final Long reservationSpecId = rentalSpecsRequest.getReservationSpecId();
        return rentalSpecsRequest.getPropertyNumbers().stream()
                .map(propertyNumber -> mapToRentalSpec(reservationId, reservationSpecId, propertyNumber))
                .toList();
    }

    private RentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId, final String propertyNumber) {
        return RentalSpec.builder()
                .reservationId(reservationId)
                .reservationSpecId(reservationSpecId)
                .propertyNumber(propertyNumber)
                .build();
    }

    @Transactional(readOnly = true)
    public ReservationsWithRentalSpecsAndMemberNumberResponse getReservationsWithRentalSpecsByStartDate(final LocalDate localDate) {
        final Set<ReservationWithMemberNumber> reservations = reservationService.getReservationsByStartDate(localDate);
        final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(reservations);
        final List<RentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
        return ReservationsWithRentalSpecsAndMemberNumberResponse.of(reservations, rentalSpecs);
    }

    private Set<Long> getAcceptedReservationSpecIds(final Collection<ReservationWithMemberNumber> reservationsWithMemberNumber) {
        final List<Reservation> reservations = reservationsWithMemberNumber.stream()
                .map(ReservationWithMemberNumber::getReservation)
                .toList();
        return Set.copyOf(new Reservations(reservations)
                .getAcceptedReservationSpecIds());
    }

    @Transactional(readOnly = true)
    public ReservationsWithRentalSpecsByEndDateResponse getReservationsWithRentalSpecsByEndDate(final LocalDate endDate) {
        final OverdueReservationsWithRentalSpecsResponse overdueReservationsWithRentalSpecs = getOverdueReservationsWithRentalSpecs(endDate);
        final ReservationsWithRentalSpecsAndMemberNumberResponse reservationWithRentalSpecsByEndDate = getReservationWithRentalSpecsByEndDate(endDate);
        return new ReservationsWithRentalSpecsByEndDateResponse(overdueReservationsWithRentalSpecs, reservationWithRentalSpecsByEndDate);
    }

    private OverdueReservationsWithRentalSpecsResponse getOverdueReservationsWithRentalSpecs(final LocalDate localDate) {
        Set<ReservationWithMemberNumber> overdueReservationsWithMemberNumber = reservationService.getOverdueReservationsWithMemberNumber(localDate);
        final Set<Long> overdueReservationSpecsIds = getAcceptedReservationSpecIds(overdueReservationsWithMemberNumber);
        final List<RentalSpec> overdueRentalSpecs = rentalSpecRepository.findByReservationSpecIds(overdueReservationSpecsIds)
                .stream()
                .filter(RentalSpec::isNowRental)
                .toList();
        return OverdueReservationsWithRentalSpecsResponse.of(overdueReservationsWithMemberNumber, overdueRentalSpecs);
    }

    private ReservationsWithRentalSpecsAndMemberNumberResponse getReservationWithRentalSpecsByEndDate(final LocalDate localDate) {
        Set<ReservationWithMemberNumber> reservationsWithMemberNumber = reservationService.getReservationsWithMemberNumberByEndDate(localDate);
        final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(reservationsWithMemberNumber);
        final List<RentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
        return ReservationsWithRentalSpecsAndMemberNumberResponse.of(reservationsWithMemberNumber, rentalSpecs);
    }

    @Transactional
    public void returnRental(final ReturnRentalRequest returnRentalRequest) {
        Rental rental = getRental(returnRentalRequest);
        final Map<Long, RentalSpecStatus> returnRequest = returnRentalRequest.getRentalSpecs().stream()
                .collect(toMap(ReturnRentalSpecRequest::getId, ReturnRentalSpecRequest::getStatus));
        for (Long rentalSpecId : returnRequest.keySet()) {
            final RentalSpecStatus status = returnRequest.get(rentalSpecId);
            rental.returnByRentalSpecId(rentalSpecId, status);
            setPenaltyAndItemAvailable(rental.getRentalSpec(rentalSpecId));
        }
        rental.setReservationStatusAfterReturn();
    }

    private Rental getRental(final ReturnRentalRequest returnRentalRequest) {
        final List<RentalSpec> rentalSpecList = rentalSpecRepository.findByReservationId(returnRentalRequest.getReservationId());
        final Reservation reservation = reservationService.getReservationWithReservationSpecsById(returnRentalRequest.getReservationId());
        return Rental.of(rentalSpecList, reservation);
    }

    private void setPenaltyAndItemAvailable(final RentalSpec rentalSpec) {
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
}
