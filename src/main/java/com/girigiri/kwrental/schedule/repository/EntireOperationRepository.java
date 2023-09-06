package com.girigiri.kwrental.schedule.repository;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.schedule.domain.EntireOperation;

public interface EntireOperationRepository extends Repository<EntireOperation, Long>, EntireOperationRepositoryCustom {
	EntireOperation save(final EntireOperation entireOperation);
}
