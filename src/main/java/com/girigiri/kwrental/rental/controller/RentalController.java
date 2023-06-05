package com.girigiri.kwrental.rental.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto;
import com.girigiri.kwrental.rental.service.RentalService;

@RequestMapping("/api/rentals")
@RestController
public class RentalController {


    private final RentalService rentalService;

    public RentalController(final RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping(params = {"from", "to"})
    public EquipmentRentalsDto getEquipmentRentalsBetween(@Login final SessionMember sessionMember,
        final LocalDate from, final LocalDate to) {
        return rentalService.getEquipmentRentalsBetweenDate(sessionMember.getId(), from, to);
    }

    @GetMapping(path = "/labRooms", params = {"from", "to"})
    public LabRoomRentalsDto getLabRoomRentalsBetween(@Login final SessionMember sessionMember, final LocalDate from,
        final LocalDate to) {
        return rentalService.getLabRoomRentalsBetweenDate(sessionMember.getId(), from, to);
    }
}
