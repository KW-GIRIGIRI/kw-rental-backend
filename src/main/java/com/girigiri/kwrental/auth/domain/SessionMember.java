package com.girigiri.kwrental.auth.domain;

import com.girigiri.kwrental.auth.exception.SessionMemberInconsistencyException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

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

    public void validateConsistency(final Member member) {
        if (!Objects.equals(this.id, member.getId())
                || !Objects.equals(this.memberNumber, member.getMemberNumber())
                || !Objects.equals(this.role, member.getRole())) {
            throw new SessionMemberInconsistencyException();
        }
    }
}
