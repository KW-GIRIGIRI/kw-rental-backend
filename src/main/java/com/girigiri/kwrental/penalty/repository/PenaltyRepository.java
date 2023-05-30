package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import org.springframework.data.repository.Repository;

public interface PenaltyRepository extends Repository<Penalty, Long>, PenaltyRepositoryCustom {

    Penalty save(Penalty penalty);
}
