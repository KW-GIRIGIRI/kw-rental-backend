package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.testsupport.fixture.PenaltyFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class PenaltyRepositoryTest {

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Test
    @DisplayName("특정 회원의 현재 진행 중인 페널티를 조회한다.")
    void findOngoingPenalty() {
        // given
        final LocalDate now = LocalDate.now();
        final PenaltyPeriod penaltyPeriod1 = new PenaltyPeriod(now, now.plusDays(3));
        final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod1).build());
        final PenaltyPeriod penaltyPeriod2 = new PenaltyPeriod(now.minusDays(2), now.minusDays(1));
        final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod2).build());
        final PenaltyPeriod penaltyPeriod3 = new PenaltyPeriod(now.minusDays(2), now);
        final Penalty penalty3 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod3).build());
        final Penalty penalty4 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(2L).period(penaltyPeriod1).build());

        // when
        final List<Penalty> actual = penaltyRepository.findByOngoingPenalties(1L);

        // then
        assertThat(actual).containsExactlyInAnyOrder(penalty1, penalty3);
    }
}
