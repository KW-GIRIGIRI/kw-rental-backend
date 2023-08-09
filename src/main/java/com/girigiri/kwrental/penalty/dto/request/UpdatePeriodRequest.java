package com.girigiri.kwrental.penalty.dto.request;

import com.girigiri.kwrental.penalty.domain.PenaltyStatus;

import jakarta.validation.constraints.NotNull;

public record UpdatePeriodRequest(@NotNull PenaltyStatus status) {
}
