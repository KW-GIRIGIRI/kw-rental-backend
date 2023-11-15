package com.girigiri.kwrental.operation.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.util.List;

import com.girigiri.kwrental.testsupport.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.common.config.JpaConfig;
import com.girigiri.kwrental.operation.domain.Schedule;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

@RepositoryTest
class ScheduleRepositoryTest {

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Test
	@DisplayName("저장된 일정을 모두 삭제한다.")
	void deleteAllSchedules() {
		// given
		final Schedule mondaySchedule = ScheduleFixture.create(DayOfWeek.MONDAY);
		scheduleRepository.saveAll(List.of(mondaySchedule));

		// when
		final long actual = scheduleRepository.deleteAllSchedules();

		// then
		assertThat(actual).isOne();
	}
}