package com.girigiri.kwrental.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MediaMajorNumberTest {

	@ParameterizedTest
	@CsvSource({"1111323111,true", "1111317111,true", "1111111111,false"})
	@DisplayName("입력받은 학번이 미디어 학번인지 확인한다.")
	void isMediaMajor(final String memberNumber, final boolean expect) {
		// given, when
		final boolean actual = MediaMajorNumber.isMediaMajor(memberNumber);

		// then
		assertThat(actual).isEqualTo(expect);
	}
}