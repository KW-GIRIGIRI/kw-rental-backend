package com.girigiri.kwrental.penalty.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserPenaltiesResponse {

    private List<UserPenaltyResponse> penalties;

    private UserPenaltiesResponse() {
    }

    public UserPenaltiesResponse(final List<UserPenaltyResponse> penalties) {
        this.penalties = penalties;
    }
}
