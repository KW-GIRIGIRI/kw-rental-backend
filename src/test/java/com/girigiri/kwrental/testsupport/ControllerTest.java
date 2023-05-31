package com.girigiri.kwrental.testsupport;

import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.auth.controller.SessionCookieSupport;
import com.girigiri.kwrental.auth.interceptor.UserMemberArgumentResolver;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.auth.service.AuthService;
import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.labroom.service.LabRoomService;
import com.girigiri.kwrental.penalty.service.PenaltyServiceImpl;
import com.girigiri.kwrental.rental.service.RentalService;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(UserMemberArgumentResolver.class)
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
    protected RentalService rentalService;

    @MockBean
    protected ReservationService reservationService;

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
}
