package com.girigiri.kwrental.operation.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.common.config.JpaConfig;
import com.girigiri.kwrental.operation.domain.EntireOperation;
import com.girigiri.kwrental.testsupport.fixture.EntireOperationFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaConfig.class)
class EntireOperationRepositoryTest {
	@Autowired
	private EntireOperationRepository entireOperationRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("전체 운영을 업데이트한다.")
	void updateEntireOperation() {
		// given
		final EntireOperation entireOperation = EntireOperationFixture.create(false);
		entireOperationRepository.save(entireOperation);

		// when
		final long actual = entireOperationRepository.updateEntireOperation(true);
		entityManager.refresh(entireOperation);

		// then
		assertThat(entireOperation.isRunning()).isTrue();
		assertThat(actual).isOne();
	}

	@Test
	@DisplayName("전체 운영이 저장된 기록이 있는 지 확인한다.")
	void exists() {
		// given
		final EntireOperation entireOperation = EntireOperationFixture.create(true);
		entireOperationRepository.save(entireOperation);

		// when
		final boolean actual = entireOperationRepository.exists();

		// then
		assertThat(actual).isTrue();
	}
}