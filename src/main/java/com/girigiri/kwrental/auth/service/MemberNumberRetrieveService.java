package com.girigiri.kwrental.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.auth.domain.MediaMajorNumber;
import com.girigiri.kwrental.auth.dto.request.KwangwoonMemberRetrieveRequest;
import com.girigiri.kwrental.auth.dto.request.MemberNumberResponse;
import com.girigiri.kwrental.auth.dto.response.KwangwoonMemberResponse;
import com.girigiri.kwrental.auth.exception.MemberNumberRetrieveException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberNumberRetrieveService {

	private final KwangwoonMemberService kwangwoonMemberService;

	public MemberNumberResponse retrieveMediaMemberNumber(
		final KwangwoonMemberRetrieveRequest kwangwoonMemberRetrieveRequest) {
		final String memberNumber = getMemberNumber(kwangwoonMemberRetrieveRequest);
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

	private void validateMajorNumber(final String memberNumber) {
		final boolean isMediaMajor = MediaMajorNumber.isMediaMajor(memberNumber);
		if (!isMediaMajor) {
			throw new MemberNumberRetrieveException("미디어커뮤니케이션 학번이 아닙니다.");
		}
	}
}
