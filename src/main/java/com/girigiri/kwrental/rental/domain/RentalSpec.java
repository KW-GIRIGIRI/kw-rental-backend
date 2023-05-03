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
@Builder
public class RentalSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationSpecId;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private String propertyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RentalStatus status = RentalStatus.RENTED;

    @CreatedDate
    private LocalDateTime acceptDateTime;

    private LocalDateTime returnDateTime;

    protected RentalSpec() {
    }

    private RentalSpec(final Long id, final Long reservationSpecId, final Long reservationId, final String propertyNumber, final RentalStatus status, final LocalDateTime acceptDateTime, final LocalDateTime returnDateTime) {
        this.id = id;
        this.reservationSpecId = reservationSpecId;
        this.reservationId = reservationId;
        this.propertyNumber = propertyNumber;
        this.status = status;
        this.acceptDateTime = acceptDateTime;
        this.returnDateTime = returnDateTime;
    }

    public boolean isNowRental() {
        return acceptDateTime != null && returnDateTime == null;
    }

    public void setStatus(final RentalStatus status) {
        this.status = status;
    }
}
