package com.girigiri.kwrental.rental.controller;

import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/rentals")
@Validated
public class RentalController {

    private final RentalService rentalService;

    public RentalController(final RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<?> rent(@RequestBody final CreateRentalRequest createRentalRequest) {
        rentalService.rent(createRentalRequest);
        return ResponseEntity
                .created(URI.create("/api/rentals?reservationId=" + createRentalRequest.getReservationId()))
                .build();
    }
}
