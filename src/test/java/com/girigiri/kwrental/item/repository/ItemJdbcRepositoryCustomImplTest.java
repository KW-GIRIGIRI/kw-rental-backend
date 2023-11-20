package com.girigiri.kwrental.item.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.jpa.ItemJdbcRepositoryCustomImpl;
import com.girigiri.kwrental.testsupport.RepositoryTest;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@RepositoryTest
class ItemJdbcRepositoryCustomImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ItemJdbcRepositoryCustomImpl itemJdbcRepositoryCustomImpl;

    @BeforeEach
    void beforeEach() {
        itemJdbcRepositoryCustomImpl = new ItemJdbcRepositoryCustomImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("품목을 bulk insert한다.")
    void saveAll() {
        // given
        final Item item1 = ItemFixture.builder().propertyNumber("12345678").build();
        final Item item2 = ItemFixture.builder().propertyNumber("87654321").build();

        // when
        final int expect = itemJdbcRepositoryCustomImpl.saveAll(List.of(item1, item2));

        // then
        assertThat(expect).isEqualTo(2);
    }
}
