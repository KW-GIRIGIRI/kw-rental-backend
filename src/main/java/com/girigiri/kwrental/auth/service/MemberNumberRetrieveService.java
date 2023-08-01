package com.girigiri.kwrental.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.auth.dto.request.KwangwoonMemberRetrieveRequest;
import com.girigiri.kwrental.auth.dto.request.MemberNumberResponse;
import com.girigiri.kwrental.auth.dto.response.KwangwoonMemberResponse;
import com.girigiri.kwrental.auth.exception.MemberNumberRetrieveException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberNumberRetrieveService {

	private static final String OLD_MEDIA_MAJOR_NUMBER = "317";
	private static final String MEDIA_MAJOR_NUMBER = "323";
	private static final int MAJOR_BEGIN_INDEX = 4;
	private static final int MAJOR_END_INDEX = 7;
	private static final int MEMBER_NUMBER_LENGTH = 10;
	private final KwangwoonMemberService kwangwoonMemberService;

	public MemberNumberResponse retrieve(final KwangwoonMemberRetrieveRequest kwangwoonMemberRetrieveRequest) {
		final String memberNumber = getMemberNumber(kwangwoonMemberRetrieveRequest);
		validateMemberNumberLength(memberNumber);
		validateMajorNumber(memberNumber);
		return new MemberNumberResponse(memberNumber);
	}

	private String getMemberNumber(final KwangwoonMemberRetrieveRequest kwangwoonMemberRetrieveRequest) {
		final List<KwangwoonMemberResponse> kwangwoonMemberResponses = kwangwoonMemberService.retrieve(
			kwangwoonMemberRetrieveRequest);
		if (kwangwoonMemberResponses.size() != 1) {
			throw new MemberNumberRetrieveException("조회 결과가 단일하지 않습니다.");
		}
		return kwangwoonMemberResponses.iterator().next().hakbun();
	}

	private void validateMemberNumberLength(final String memberNumber) {
		if (memberNumber.length() != MEMBER_NUMBER_LENGTH) {
			throw new MemberNumberRetrieveException("조회된 학번이 10글자가 아닙니다.");
		}
	}

	private void validateMajorNumber(final String memberNumber) {
		final String majorNumber = memberNumber.substring(MAJOR_BEGIN_INDEX, MAJOR_END_INDEX);
		final boolean isMediaMajor =
			majorNumber.equals(OLD_MEDIA_MAJOR_NUMBER) || majorNumber.equals(MEDIA_MAJOR_NUMBER);
		if (!isMediaMajor) {
			throw new MemberNumberRetrieveException("미디어커뮤니케이션 학번이 아닙니다.");
		}
	}
}
