package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;

class InventoryAcceptanceTest extends AcceptanceTest {

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("기자재를 담는 기자재로 등록한다.")
    void addInventory() {
        // given
        Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());

        AddInventoryRequest request = AddInventoryRequest.builder()
                .equipmentId(equipment.getId())
                .rentalStartDate(LocalDate.now().plusDays(1))
                .rentalEndDate(LocalDate.now().plusDays(2))
                .amount(1)
                .build();

        // when
        RestAssured.given(requestSpec)
                .body(request)
                .contentType(ContentType.JSON)
                .when().log().all().post("/api/inventories")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/inventories/"));
    }
}
