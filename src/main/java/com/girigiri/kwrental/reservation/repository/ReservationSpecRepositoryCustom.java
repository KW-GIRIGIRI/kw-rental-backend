package com.girigiri.kwrental.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationWithMemberNumberResponse;

public interface ReservationSpecRepositoryCustom {
    List<ReservationSpec> findOverlappedReservedOrRentedByPeriod(Long equipmentId, RentalPeriod rentalPeriod);

    List<ReservationSpec> findOverlappedReservedOrRentedInclusive(Long equipmentId, LocalDate start, LocalDate end);

    List<ReservedAmount> findRentalAmountsByEquipmentIds(List<Long> equipmentIds, LocalDate date);

    List<ReservationSpec> findByStartDateBetween(Long equipmentId, LocalDate start, LocalDate end);

    void adjustAmountAndStatus(ReservationSpec reservationSpec);

    Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenAccept(LocalDate date);

    Set<EquipmentReservationWithMemberNumber> findOverdueEquipmentReservationWhenReturn(LocalDate date);

    Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenReturn(LocalDate date);

    Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationsWhenAccept(LocalDate date);

    Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationWhenReturn(LocalDate date);

    void updateStatusByIds(List<Long> ids, ReservationSpecStatus status);

    HistoryStatResponse findHistoryStat(String name, LocalDate startDate, LocalDate endDate);
}
