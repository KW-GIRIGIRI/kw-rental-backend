package com.girigiri.kwrental.asset.labroom.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.testsupport.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.common.config.JpaConfig;
import com.girigiri.kwrental.testsupport.fixture.LabRoomDailyBanFixture;

@RepositoryTest
class LabRoomDailyBanRepositoryTest {

	@Autowired
	private LabRoomDailyBanRepository labRoomDailyBanRepository;

	@Test
	@DisplayName("특정 기간에 해당하는 특정 랩실의 금지를 조회한다.")
	void findByLabRoomIdAndInclusive() {
		// given
		final LocalDate now = LocalDate.now();
		final LabRoomDailyBan ban1 = labRoomDailyBanRepository.save(
			LabRoomDailyBanFixture.builder().labRoomId(1L).banDate(now).build());
		final LabRoomDailyBan ban2 = labRoomDailyBanRepository.save(
			LabRoomDailyBanFixture.builder().labRoomId(1L).banDate(now.plusDays(1)).build());
		final LabRoomDailyBan ban3 = labRoomDailyBanRepository.save(
			LabRoomDailyBanFixture.builder().labRoomId(1L).banDate(now.plusDays(2)).build());
		final LabRoomDailyBan ban4 = labRoomDailyBanRepository.save(
			LabRoomDailyBanFixture.builder().labRoomId(1L).banDate(now.plusDays(3)).build());

		// when
		final List<LabRoomDailyBan> actual = labRoomDailyBanRepository.findByLabRoomIdAndInclusive(1L,
			now.plusDays(1), now.plusDays(2));

		// then
		assertThat(actual).containsExactly(ban2, ban3);
	}
}
