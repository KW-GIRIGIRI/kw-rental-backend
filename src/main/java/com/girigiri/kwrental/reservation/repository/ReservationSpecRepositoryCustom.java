package com.girigiri.kwrental.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.ReservedAmount;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse.LabRoomReservationWithMemberNumberResponse;

public interface ReservationSpecRepositoryCustom {
    List<ReservationSpec> findOverlappedReservedOrRentedByPeriod(Long equipmentId, RentalPeriod rentalPeriod);

    List<ReservationSpec> findOverlappedReservedOrRentedInclusive(Long equipmentId, LocalDate start, LocalDate end);

    List<ReservedAmount> findRentalAmountsByAssetIds(List<Long> assetIds, LocalDate date);

    List<ReservationSpec> findNotCanceldByStartDateBetween(Long equipmentId, LocalDate start, LocalDate end);

    void adjustAmountAndStatus(ReservationSpec reservationSpec);

    Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenAccept(LocalDate date);

    Set<EquipmentReservationWithMemberNumber> findOverdueEquipmentReservationWhenReturn(LocalDate date);

    Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenReturn(LocalDate date);

    Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationsWhenAccept(LocalDate date);

    Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationWhenReturn(LocalDate date);

    void updateStatusByIds(List<Long> ids, ReservationSpecStatus status);

    HistoryStatResponse findHistoryStat(String name, LocalDate startDate, LocalDate endDate);

    List<ReservationSpec> findReservedOrRentedByAssetId(Long assetId);
}
