package com.girigiri.kwrental.operation.dto.request;

import java.time.DayOfWeek;
import java.util.List;

import lombok.Builder;

@Builder
public record PutSchedulesRequest(
	List<DayOfWeek> schedules
) {
}

