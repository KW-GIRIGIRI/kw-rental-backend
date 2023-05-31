package com.girigiri.kwrental.penalty.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PenaltyHistoryPageResponse {
    private List<String> endPoints;
    private Integer page;
    private List<PenaltyHistoryResponse> penalties;

    private PenaltyHistoryPageResponse() {
    }

    public PenaltyHistoryPageResponse(final List<String> endPoints, final Integer page, final List<PenaltyHistoryResponse> penalties) {
        this.endPoints = endPoints;
        this.page = page;
        this.penalties = penalties;
    }
}
