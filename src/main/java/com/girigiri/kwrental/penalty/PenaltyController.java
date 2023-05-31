package com.girigiri.kwrental.penalty;

import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.interceptor.UserMember;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.rental.service.PenaltyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    public PenaltyController(final PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    @GetMapping
    public UserPenaltiesResponse getUserPenaltiesResponse(@UserMember final SessionMember sessionMember) {
        return penaltyService.getPenalties(sessionMember.getId());
    }
}
