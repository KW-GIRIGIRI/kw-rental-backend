package com.girigiri.kwrental.reservation.domain;

import jakarta.persistence.*;
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

    public Reservation(final Long id, final List<RentalSpec> rentalSpecs) {
        this.id = id;
        this.rentalSpecs = rentalSpecs;
    }

    public Reservation() {

    }
}
