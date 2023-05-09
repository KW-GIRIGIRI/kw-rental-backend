package com.girigiri.kwrental.rental.controller;

import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsAndMemberNumberResponse;
import com.girigiri.kwrental.rental.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/rentals")
@Validated
public class AdminRentalController {

    private final RentalService rentalService;

    public AdminRentalController(final RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping(params = "startDate")
    public ReservationsWithRentalSpecsAndMemberNumberResponse getReservationsWithRentalSpecsByStartDate(final LocalDate startDate) {
        return rentalService.getReservationsWithRentalSpecsByStartDate(startDate);
    }

    @GetMapping(params = "endDate")
    public ReservationsWithRentalSpecsByEndDateResponse getReservationWithRentalSpecsByEndDate(final LocalDate endDate) {
        return rentalService.getReservationsWithRentalSpecsByEndDate(endDate);
    }

    @PostMapping
    public ResponseEntity<?> rent(@RequestBody final CreateRentalRequest createRentalRequest) {
        rentalService.rent(createRentalRequest);
        return ResponseEntity
                .created(URI.create("/api/rentals?reservationId=" + createRentalRequest.getReservationId()))
                .build();
    }

    @PatchMapping("/returns")
    public ResponseEntity<?> returnRental(@RequestBody final ReturnRentalRequest returnRentalRequest) {
        rentalService.returnRental(returnRentalRequest);
        return ResponseEntity.noContent().build();
    }
}
