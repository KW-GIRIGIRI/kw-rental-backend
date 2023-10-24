package com.girigiri.kwrental.reservation.domain.entity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalDateTime {

	private static final RentalDateTime NULL = new RentalDateTime();
	private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

	private Instant instant;

	private RentalDateTime(final Instant instant) {
		this.instant = instant.truncatedTo(ChronoUnit.MILLIS);
	}

	public static RentalDateTime now() {
		return new RentalDateTime(Instant.now());
	}

	public static RentalDateTime from(final LocalDateTime localDateTime) {
		return new RentalDateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static RentalDateTime from(final LocalDate localDate) {
		final Instant instant = localDate.atStartOfDay(SEOUL_ZONE_ID).toInstant();
		return new RentalDateTime(instant);
	}

	public RentalDateTime calculateDay(final int days) {
		final Instant instant =
			days < 0 ? this.instant.minus(Duration.ofDays(-days)) : this.instant.plus(Duration.ofDays(days));
		return new RentalDateTime(instant);
	}

	public LocalDateTime toLocalDateTime() {
		return this.instant.atZone(SEOUL_ZONE_ID).toLocalDateTime().truncatedTo(ChronoUnit.MILLIS);
	}

	public LocalDate toLocalDate() {
		return this.instant.atZone(SEOUL_ZONE_ID).toLocalDate();
	}
}
