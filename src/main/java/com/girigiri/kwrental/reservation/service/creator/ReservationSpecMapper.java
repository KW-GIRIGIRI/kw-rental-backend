package com.girigiri.kwrental.reservation.service.creator;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;

public interface ReservationSpecMapper {
	List<ReservationSpec> map(Long memberId);
}
