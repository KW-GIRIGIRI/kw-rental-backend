package com.girigiri.kwrental.operation.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.operation.domain.EntireOperation;

public interface EntireOperationRepository extends Repository<EntireOperation, Long>, EntireOperationRepositoryCustom {
	EntireOperation save(final EntireOperation entireOperation);

	List<EntireOperation> findAll();
}
