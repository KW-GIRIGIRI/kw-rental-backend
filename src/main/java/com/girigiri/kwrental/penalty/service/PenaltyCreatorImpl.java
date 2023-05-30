package com.girigiri.kwrental.penalty.service;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.service.PenaltyCreator;
import org.springframework.stereotype.Service;

@Service
public class PenaltyCreatorImpl implements PenaltyCreator {

    private final PenaltyRepository penaltyRepository;

    public PenaltyCreatorImpl(final PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    public void create(final Long memberId, final Long reservationId, final Long reservationSpecId, final Long rentalSpecId, final RentalSpecStatus status) {
        final int penaltyCountBefore = countPenaltyFromDifferentReservation(memberId, reservationId);
        final Penalty penalty = Penalty.builder()
                .reason(PenaltyReason.from(status))
                .period(PenaltyPeriod.fromPenaltyCount(penaltyCountBefore))
                .rentalSpecId(rentalSpecId)
                .reservationSpecId(reservationSpecId)
                .memberId(memberId)
                .reservationId(reservationId)
                .build();
        penaltyRepository.save(penalty);
    }

    private int countPenaltyFromDifferentReservation(final Long memberId, final Long reservationId) {
        return (int) penaltyRepository.findByMemberId(memberId)
                .stream()
                .filter(it -> !it.getReservationId().equals(reservationId))
                .count();
    }
}
