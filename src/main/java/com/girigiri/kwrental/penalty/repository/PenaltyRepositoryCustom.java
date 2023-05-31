package com.girigiri.kwrental.penalty.repository;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PenaltyRepositoryCustom {
    List<Penalty> findByOngoingPenalties(Long memberId);

    UserPenaltiesResponse findUserPenaltiesResponseByMemberId(Long memberId);

    Page<PenaltyHistoryResponse> findPenaltyHistoryPageResponse(Pageable pageable);
}
