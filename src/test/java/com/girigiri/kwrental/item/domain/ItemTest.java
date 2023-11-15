package com.girigiri.kwrental.item.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

class ItemTest {

    @ParameterizedTest
    @CsvSource({"12345678,false", "87654321,true", ",false"})
    @DisplayName("입력받은 값으로 자산번호으로 변경 가능한 값인지 판단한다.")
    void updatePropertyNumber(String propertyNumber, boolean expect) {
        // given
        Item item = ItemFixture.builder().propertyNumber("12345678").build();

        // when
        final boolean actual = item.canUpdatePropertyNumberTo(propertyNumber);

        // then
        assertThat(actual).isEqualTo(expect);
    }
}