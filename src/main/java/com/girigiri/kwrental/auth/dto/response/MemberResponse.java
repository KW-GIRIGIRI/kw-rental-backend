package com.girigiri.kwrental.auth.dto.response;

import com.girigiri.kwrental.auth.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String memberNumber;

    private MemberResponse() {
    }

    private MemberResponse(final Long id, final String name, final String email, final String phoneNumber, final String memberNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.memberNumber = memberNumber;
    }

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
