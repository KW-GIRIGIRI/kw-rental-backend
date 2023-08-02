package com.girigiri.kwrental.auth.domain;

import java.util.Arrays;

import com.girigiri.kwrental.auth.exception.MemberNumberException;

public enum MediaMajorNumber {
	OLD_MEDIA_COMMUNICATION("317"),
	MEDIA_COMMUNICATION("323");

	private static final int MEMBER_NUMBER_LENGTH = 10;
	private static final int MAJOR_BEGIN_INDEX = 4;
	private static final int MAJOR_END_INDEX = 7;

	private final String value;

	MediaMajorNumber(final String value) {
		this.value = value;
	}

	public static boolean isMediaMajor(final String memberNumber) {
		validateMemberNumberLength(memberNumber);
		final String majorNumber = extractMajorNumber(memberNumber);
		return Arrays.stream(values())
			.anyMatch(it -> it.value.equals(majorNumber));
	}

	private static void validateMemberNumberLength(final String memberNumber) {
		if (memberNumber.length() != MEMBER_NUMBER_LENGTH) {
			throw new MemberNumberException("조회된 학번이 10글자가 아닙니다.");
		}
	}

	private static String extractMajorNumber(final String memberNumber) {
		return memberNumber.substring(MAJOR_BEGIN_INDEX, MAJOR_END_INDEX);
	}
}
