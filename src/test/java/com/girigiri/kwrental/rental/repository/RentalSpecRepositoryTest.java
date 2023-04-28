package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaConfig.class)
class RentalSpecRepositoryTest {

    @Autowired
    private RentalSpecRepository rentalSpecRepository;

    @Test
    @DisplayName("대여 상세를 모두 저장한다.")
    void saveAll() {
        // given
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().acceptDateTime(null).propertyNumber("12345678").build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().acceptDateTime(null).propertyNumber("87654321").build();

        // when
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        // then
        assertAll(
                () -> assertThat(rentalSpec1.getId()).isNotNull(),
                () -> assertThat(rentalSpec2.getId()).isNotNull(),
                () -> assertThat(rentalSpec1.getAcceptDateTime()).isNotNull(),
                () -> assertThat(rentalSpec2.getAcceptDateTime()).isNotNull()
        );
    }
}