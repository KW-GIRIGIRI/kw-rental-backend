package com.girigiri.kwrental.operation.dto.response;

import java.time.DayOfWeek;
import java.util.List;

public record SchedulesResponse(List<DayOfWeek> schedules) {
}
