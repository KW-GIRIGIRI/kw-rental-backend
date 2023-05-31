package com.girigiri.kwrental.penalty.service;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.service.PenaltyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;

    public PenaltyServiceImpl(final PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOngoingPenalty(final Long memberId) {
        final List<Penalty> ongoingPenalties = penaltyRepository.findByOngoingPenalties(memberId);
        return ongoingPenalties.size() > 0;
    }

    @Override
    @Transactional
    public UserPenaltiesResponse getPenalties(final Long memberId) {
        return penaltyRepository.findUserPenaltiesResponseByMemberId(memberId);
    }
}
