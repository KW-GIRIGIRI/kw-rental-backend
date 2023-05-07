package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST, mappedBy = "reservation")
    private List<ReservationSpec> reservationSpecs;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private boolean terminated = false;

    private LocalDateTime acceptDateTime;

    @Column(nullable = false)
    private Long memberId;


    protected Reservation() {

    }

    @Builder
    private Reservation(final Long id, final List<ReservationSpec> reservationSpecs, final String name, final String email, final String phoneNumber, final String purpose, final boolean terminated, final LocalDateTime acceptDateTime, final Long memberId) {
        this.id = id;
        this.terminated = terminated;
        this.acceptDateTime = acceptDateTime;
        this.memberId = memberId;
        validateReservationSpec(reservationSpecs);
        this.reservationSpecs = reservationSpecs;
        reservationSpecs.forEach(it -> it.setReservation(this));
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.purpose = purpose;
    }

    private void validateReservationSpec(final List<ReservationSpec> reservationSpecs) {
        if (reservationSpecs == null || reservationSpecs.isEmpty()) {
            throw new ReservationException("대여 상세 내용이 없습니다.");
        }
        final RentalPeriod period = reservationSpecs.get(0).getPeriod();
        reservationSpecs.forEach(spec -> {
            if (!spec.hasPeriod(period)) throw new ReservationException("대여 상세 내용들의 대여 기간이 통일되지 않았습니다.");
        });
    }

    public void acceptAt(final LocalDateTime acceptDateTime) {
        this.acceptDateTime = acceptDateTime;
    }

    public boolean isAccepted() {
        return this.acceptDateTime != null;
    }

    public void updateIfTerminated() {
        this.terminated = reservationSpecs.stream()
                .map(ReservationSpec::getStatus)
                .allMatch(it ->
                        it != ReservationSpecStatus.RESERVED
                                && it != ReservationSpecStatus.RENTED
                                && it != ReservationSpecStatus.OVERDUE_RENTED
                );
    }
}
