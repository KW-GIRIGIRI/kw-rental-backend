package com.girigiri.kwrental.rental.controller;

import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.interceptor.UserMember;
import com.girigiri.kwrental.rental.dto.response.RentalsDto;
import com.girigiri.kwrental.rental.service.RentalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequestMapping("/api/rentals")
@RestController
public class RentalController {


    private final RentalService rentalService;

    public RentalController(final RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping(params = {"from", "to"})
    public RentalsDto getRentalsBetween(@UserMember final SessionMember sessionMember, final LocalDate from, final LocalDate to) {
        return rentalService.getRentalsBetweenDate(sessionMember.getId(), from, to);
    }
}
