package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends Repository<Penalty, Long>, PenaltyRepositoryCustom {
    Optional<Penalty> findByRentalSpecId(Long rentalSpecId);

    Optional<Penalty> findById(Long id);

    Penalty save(Penalty penalty);

    List<Penalty> findByMemberId(Long memberId);

    void deleteById(Long id);
}
