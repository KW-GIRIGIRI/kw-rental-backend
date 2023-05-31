package com.girigiri.kwrental.penalty.service;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.domain.PenaltyStatus;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyStatusResponse;
import com.girigiri.kwrental.penalty.exception.PenaltyNotFoundException;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.service.PenaltyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public UserPenaltiesResponse getPenalties(final Long memberId) {
        return penaltyRepository.findUserPenaltiesResponseByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public UserPenaltyStatusResponse getPenaltyStatus(final Long memberId) {
        final List<Penalty> penalties = penaltyRepository.findByOngoingPenalties(memberId);
        final Optional<PenaltyPeriod> maxFarPeriod = penalties.stream()
                .map(Penalty::getPeriod)
                .max(Comparator.comparing(PenaltyPeriod::getEndDate));
        return maxFarPeriod.map(period -> new UserPenaltyStatusResponse(false, period.getEndDate()))
                .orElseGet(() -> new UserPenaltyStatusResponse(true, null));
    }

    @Transactional(readOnly = true)
    public Page<PenaltyHistoryResponse> getPenaltyHistoryPage(final Pageable pageable) {
        return penaltyRepository.findPenaltyHistoryPageResponse(pageable);
    }

    @Transactional
    public void updatePeriod(final Long id, final PenaltyStatus status) {
        final Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(PenaltyNotFoundException::new);
        penalty.updatePeriodByStatus(status);
    }
}
