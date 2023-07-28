package com.girigiri.kwrental.testsupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.girigiri.kwrental.asset.equipment.service.EquipmentService;
import com.girigiri.kwrental.asset.labroom.service.LabRoomService;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.auth.argumentresolver.LoginArgumentResolver;
import com.girigiri.kwrental.auth.controller.SessionCookieSupport;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.auth.service.AuthService;
import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.penalty.service.PenaltyServiceImpl;
import com.girigiri.kwrental.rental.service.RentalViewService;
import com.girigiri.kwrental.rental.service.rent.RentalRentService;
import com.girigiri.kwrental.rental.service.restore.EquipmentRentalRestoreService;
import com.girigiri.kwrental.rental.service.restore.LabRoomRentalRestoreService;
import com.girigiri.kwrental.reservation.service.ReservationCancelService;
import com.girigiri.kwrental.reservation.service.ReservationViewService;
import com.girigiri.kwrental.reservation.service.reserve.EquipmentReserveService;
import com.girigiri.kwrental.reservation.service.reserve.LabRoomReserveService;

@WebMvcTest
@Import(LoginArgumentResolver.class)
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected EquipmentService equipmentService;

    @MockBean
    protected MultiPartFileHandler multiPartFileHandler;

    @MockBean
    protected InventoryService inventoryService;

    @MockBean
    protected ItemService itemService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected RentalViewService rentalViewService;

    @MockBean
    protected MemberRepository memberRepository;

    @MockBean
    protected LabRoomService labRoomService;

    @MockBean
    protected AssetService assetService;

    @MockBean
    protected SessionCookieSupport sessionCookieSupport;

    @MockBean
    protected PenaltyServiceImpl penaltyService;

    @MockBean
    protected RentalRentService rentalRentService;

    @MockBean
    protected EquipmentRentalRestoreService equipmentRentalRestoreService;

    @MockBean
    protected LabRoomRentalRestoreService labRoomRentalRestoreService;

    @MockBean
    protected LabRoomReserveService labRoomReserveService;

    @MockBean
    protected EquipmentReserveService equipmentReserveService;

    @MockBean
    protected ReservationViewService reservationViewService;

    @MockBean
    protected ReservationCancelService reservationCancelService;
}
