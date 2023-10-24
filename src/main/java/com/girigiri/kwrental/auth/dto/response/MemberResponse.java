package com.girigiri.kwrental.auth.dto.response;

import com.girigiri.kwrental.auth.domain.Member;

import lombok.Builder;

@Builder
public record MemberResponse(

	Long id,
	String name,
	String email,
	String phoneNumber,
	String memberNumber) {

	public static MemberResponse from(final Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.memberNumber(member.getMemberNumber())
			.email(member.getEmail())
			.phoneNumber(member.getPhoneNumber())
			.build();
	}
}
