package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("대여를 등록한다.")
    void saveRental() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());
        final Inventory inventory = inventoryRepository.save(InventoryFixture.create(equipment));

        final AddReservationRequest request = AddReservationRequest.builder()
                .renterName("대여자")
                .renterEmail("djwhy5510@naver.com")
                .renterPhoneNumber("010-7301-5510")
                .rentalPurpose("필요하니까")
                .build();

        // when
        RestAssured.given(requestSpec)
                .filter(document("addReservations"))
                .body(request).contentType(ContentType.JSON)
                .when().log().all().post("/api/reservations")
                .then().log().all().statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/reservations/"));
    }
}
