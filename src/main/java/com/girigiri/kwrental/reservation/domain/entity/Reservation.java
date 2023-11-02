package com.girigiri.kwrental.reservation.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.girigiri.kwrental.reservation.exception.ReservationException;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

	@Column(nullable = false, name = "is_terminated")
	private boolean terminated = false;

	@Embedded
	@AttributeOverride(name = "instant", column = @Column(name = "accept_date_time"))
	private RentalDateTime acceptDateTime;

	@Column(nullable = false)
	private Long memberId;

	@Builder
	private Reservation(final Long id, final List<ReservationSpec> reservationSpecs, final String name,
		final String email, final String phoneNumber, final String purpose, final boolean terminated,
		final RentalDateTime acceptDateTime, final Long memberId) {
		this.id = id;
		this.terminated = terminated;
		this.acceptDateTime = acceptDateTime;
		this.memberId = memberId;
		validateReservationSpec(reservationSpecs);
		this.reservationSpecs = new ArrayList<>(reservationSpecs);
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
			if (!spec.hasPeriod(period))
				throw new ReservationException("대여 상세 내용들의 대여 기간이 통일되지 않았습니다.");
		});
	}

	public void acceptAt(final LocalDateTime acceptDateTime) {
		this.acceptDateTime = RentalDateTime.from(acceptDateTime);
	}

	public boolean isAccepted() {
		return this.acceptDateTime != null;
	}

	public void updateIfTerminated() {
		this.terminated = reservationSpecs.stream()
			.allMatch(ReservationSpec::isTerminated);
	}

	public RentalPeriod getRentalPeriod() {
		return new RentalPeriod(getStartDate(), getEndDate());
	}

	public LocalDate getStartDate() {
		if (reservationSpecs.size() == 0)
			throw new ReservationException("대여 예약 상세가 없어서 대여 예약의 시작일을 알 수 없습니다.");
		return this.reservationSpecs.get(0).getStartDate();
	}

	public LocalDate getEndDate() {
		if (reservationSpecs.size() == 0)
			throw new ReservationException("대여 예약 상세가 없어서 대여 예약의 종료일을 알 수 없습니다.");
		return this.reservationSpecs.get(0).getEndDate();
	}

	public List<ReservationSpec> getReservedReservationSpecs() {
		return this.reservationSpecs.stream()
				.filter(ReservationSpec::isReserved)
				.toList();
	}

	public boolean isOnlyRentFor(final String rentableName) {
		return this.reservationSpecs.stream()
			.allMatch(spec -> spec.isRentFor(rentableName));
	}
}
