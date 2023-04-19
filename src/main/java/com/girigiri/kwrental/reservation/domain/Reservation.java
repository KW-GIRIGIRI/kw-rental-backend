package com.girigiri.kwrental.reservation.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST, mappedBy = "reservation")
    private List<RentalSpec> rentalSpecs;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String purpose;


    protected Reservation() {

    }

    @Builder
    private Reservation(final Long id, final List<RentalSpec> rentalSpecs, final String name, final String email, final String phoneNumber, final String purpose) {
        this.id = id;
        this.rentalSpecs = rentalSpecs;
        rentalSpecs.forEach(it -> it.setReservation(this));
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.purpose = purpose;
    }
}
