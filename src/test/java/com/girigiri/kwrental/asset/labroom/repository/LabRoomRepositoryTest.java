package com.girigiri.kwrental.asset.labroom.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaConfig.class)
class LabRoomRepositoryTest {

	@Autowired
	private LabRoomRepository labRoomRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("랩실 공지사항을 업데이트 한다.")
	void updateNotice() {
		// given
		LabRoom labRoom = labRoomRepository.save(LabRoomFixture.create());

		// when
		String content = "a".repeat(10000);
		labRoomRepository.updateNotice(labRoom.getId(), content);

		// then
		entityManager.refresh(labRoom);
		Assertions.assertThat(labRoom.getNotice()).isEqualTo(content);
	}
}
