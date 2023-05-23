package com.girigiri.kwrental.rental.controller;

import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationsResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecsByItemResponse;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.service.RentalService;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
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
    public EquipmentReservationsWithRentalSpecsResponse getReservationsWithRentalSpecsByStartDate(final LocalDate startDate) {
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

    @GetMapping(value = "/returns", params = "propertyNumber")
    public RentalSpecsByItemResponse getReturnsByPropertyNumber(final String propertyNumber) {
        return rentalService.getReturnedRentalSpecs(propertyNumber);
    }

    @PostMapping("/labRooms")
    public ResponseEntity<?> rentLabRoom(@Validated @RequestBody RentLabRoomRequest rentLabRoomRequest) {
        rentalService.rentLabRoom(rentLabRoomRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/labRooms/returns")
    public ResponseEntity<?> returnLabRoom(@Validated @RequestBody ReturnLabRoomRequest returnLabRoomRequest) {
        rentalService.returnLabRoom(returnLabRoomRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/labRooms/{labRoomName}", params = "date")
    public LabRoomReservationsResponse getLabRoomReservations(@PathVariable String labRoomName, final LocalDate date) {
        return rentalService.getReturnedLabRoomReservation(labRoomName, date);
    }
}
