package com.girigiri.kwrental.rental.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class RentalSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationSpecId;

    @Column(nullable = false)
    private String propertyNumber;

    @CreatedDate
    private LocalDateTime acceptedDateTime;

    private LocalDateTime returnDateTime;

    protected RentalSpec() {
    }

    @Builder
    private RentalSpec(final Long id, final Long reservationSpecId, final String propertyNumber, final LocalDateTime acceptedDateTime, final LocalDateTime returnDateTime) {
        this.id = id;
        this.reservationSpecId = reservationSpecId;
        this.propertyNumber = propertyNumber;
        this.acceptedDateTime = acceptedDateTime;
        this.returnDateTime = returnDateTime;
    }

    public boolean isNowRental() {
        return acceptedDateTime != null && returnDateTime == null;
    }
}
