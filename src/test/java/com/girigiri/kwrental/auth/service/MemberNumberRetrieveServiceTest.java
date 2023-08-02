package com.girigiri.kwrental.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.auth.dto.request.KwangwoonMemberRetrieveRequest;
import com.girigiri.kwrental.auth.dto.request.MemberNumberResponse;
import com.girigiri.kwrental.auth.dto.response.KwangwoonMemberResponse;
import com.girigiri.kwrental.auth.exception.MemberNumberRetrieveException;

@ExtendWith(MockitoExtension.class)
class MemberNumberRetrieveServiceTest {

	@Mock
	private KwangwoonMemberService kwangwoonMemberService;
	@InjectMocks
	private MemberNumberRetrieveService memberNumberRetrieveService;

	@Test
	@DisplayName("학번 정보를 조회한다.")
	void retrieveMediaMemberNumber() {
		// given
		final KwangwoonMemberResponse response = new KwangwoonMemberResponse("gubun", "codeName",
			"sex", "2016317016", "1");
		given(kwangwoonMemberService.retrieve(any()))
			.willReturn(List.of(response));

		// when
		final MemberNumberResponse actual = memberNumberRetrieveService.retrieveMediaMemberNumber(
			new KwangwoonMemberRetrieveRequest("name", "birthday"));

		// then
		assertThat(actual).isEqualTo(new MemberNumberResponse("2016317016"));
	}

	@Test
	@DisplayName("미디어커뮤니케이션 학부 학생이 아니면 예외가 발생한다.")
	void retrieveMediaMemberNumber_notMediaMajor() {
		// given
		final KwangwoonMemberResponse response = new KwangwoonMemberResponse("gubun", "codeName",
			"sex", "1111111111", "1");
		given(kwangwoonMemberService.retrieve(any()))
			.willReturn(List.of(response));
		final KwangwoonMemberRetrieveRequest request = new KwangwoonMemberRetrieveRequest("name",
			"birthday");

		// when, then
		assertThatCode(() -> memberNumberRetrieveService.retrieveMediaMemberNumber(request))
			.isExactlyInstanceOf(MemberNumberRetrieveException.class);
	}

	@Test
	@DisplayName("klas에서 회원 조회 결과가 한건이 아니면 예외가 발생한다.")
	void retrieveMediaMemberNumber_notSingleResult() {
		// given
		given(kwangwoonMemberService.retrieve(any()))
			.willReturn(Collections.emptyList());
		final KwangwoonMemberRetrieveRequest request = new KwangwoonMemberRetrieveRequest("name",
			"birthday");

		// when
		assertThatCode(() -> memberNumberRetrieveService.retrieveMediaMemberNumber(request))
			.isExactlyInstanceOf(MemberNumberRetrieveException.class);
	}
}