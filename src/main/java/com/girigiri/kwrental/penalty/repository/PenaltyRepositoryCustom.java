package com.girigiri.kwrental.penalty.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;

public interface PenaltyRepositoryCustom {
    List<Penalty> findByOngoingPenalties(Long memberId);

    UserPenaltiesResponse findUserPenaltiesResponseByMemberId(Long memberId);

    Page<PenaltyHistoryResponse> findPenaltyHistoryPageResponse(Pageable pageable);
}
