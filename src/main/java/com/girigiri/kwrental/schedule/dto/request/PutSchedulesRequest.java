package com.girigiri.kwrental.schedule.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.girigiri.kwrental.common.LocalTimeDeserializer;

import lombok.Builder;

@Builder
public record PutSchedulesRequest(
	boolean isRunning,
	List<PutScheduleRequest> schedules
) {
	@Builder
	public record PutScheduleRequest(
		DayOfWeek dayOfWeek,
		@JsonDeserialize(using = LocalTimeDeserializer.class)
		LocalTime startAt,
		@JsonDeserialize(using = LocalTimeDeserializer.class)
		LocalTime endAt
	) {
	}
}

