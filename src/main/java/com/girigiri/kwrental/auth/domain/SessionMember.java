package com.girigiri.kwrental.auth.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {

    private Long id;
    private String memberNumber;

    private Role role;

    private SessionMember() {
    }

    public SessionMember(final Long id, final String memberNumber, final Role role) {
        this.id = id;
        this.memberNumber = memberNumber;
        this.role = role;
    }

    public static SessionMember from(final Member member) {
        return new SessionMember(member.getId(), member.getMemberNumber(), member.getRole());
    }
}
