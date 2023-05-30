package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;

import java.util.List;

public interface PenaltyRepositoryCustom {
    List<Penalty> findByOngoingPenalties(Long memberId);
}
