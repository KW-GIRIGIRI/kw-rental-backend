package com.girigiri.kwrental.rental.controller;

import com.girigiri.kwrental.rental.dto.response.reservationsbystartdate.ReservationsWithRentalSpecsByStartDateResponse;
import com.girigiri.kwrental.rental.service.RentalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {

    private final RentalService rentalService;

    public AdminRentalController(final RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ReservationsWithRentalSpecsByStartDateResponse getReservationsWithRentalSpecsByStartDate(final LocalDate startDate) {
        return rentalService.getReservationsWithRentalSpecsByStartDate(startDate);
    }
}
