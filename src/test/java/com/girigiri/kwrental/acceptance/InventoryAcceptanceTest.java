package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class InventoryAcceptanceTest extends AcceptanceTest {

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("기자재를 담는 기자재로 등록한다.")
    void addInventory() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        itemRepository.save(ItemFixture.builder().assetId(equipment.getId()).build());

        AddInventoryRequest request = AddInventoryRequest.builder()
                .equipmentId(equipment.getId())
                .rentalStartDate(LocalDate.now().plusDays(1))
                .rentalEndDate(LocalDate.now().plusDays(2))
                .amount(1)
                .build();

        // when
        RestAssured.given(requestSpec)
                .filter(document("addInventory"))
                .sessionId(sessionId)
                .body(request)
                .contentType(ContentType.JSON)
                .when().log().all().post("/api/inventories")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/inventories/"));
    }

    @Test
    @DisplayName("담은 기자재를 조회한다.")
    void getInventory() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("aaaaaaaa").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("bbbbbbbb").build());
        final Item item1 = itemRepository.save(ItemFixture.builder().propertyNumber("11111111").assetId(equipment1.getId()).build());
        final Item item2 = itemRepository.save(ItemFixture.builder().propertyNumber("22222222").assetId(equipment2.getId()).build());
        final Inventory inventory1 = inventoryRepository.save(InventoryFixture.create(equipment1, member.getId()));
        final Inventory inventory2 = inventoryRepository.save(InventoryFixture.create(equipment2, member.getId()));
        // when
        final InventoriesResponse response = RestAssured.given(requestSpec)
                .filter(document("getInventories"))
                .sessionId(sessionId)
                .when().log().all().get("/api/inventories")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(InventoriesResponse.class);

        // then
        assertThat(response.getInventories()).usingRecursiveFieldByFieldElementComparator()
                .extracting("modelName")
                .containsExactlyInAnyOrder(equipment1.getName(), equipment2.getName());
    }

    @Test
    @DisplayName("담은 기자재를 모두 제거한다.")
    void deleteAllInventories() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("aaaaaaaa").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("bbbbbbbb").build());
        itemRepository.save(ItemFixture.builder().propertyNumber("11111111").assetId(equipment1.getId()).build());
        itemRepository.save(ItemFixture.builder().propertyNumber("22222222").assetId(equipment2.getId()).build());
        inventoryRepository.save(InventoryFixture.create(equipment1, member.getId()));
        inventoryRepository.save(InventoryFixture.create(equipment2, member.getId()));

        // when, then
        RestAssured.given(requestSpec)
                .filter(document("deleteAllInventories"))
                .sessionId(sessionId)
                .when().log().all().delete("/api/inventories")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("특정 담은 기자재를 모두 제거한다.")
    void deleteInventory() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("aaaaaaaa").build());
        itemRepository.save(ItemFixture.builder().propertyNumber("11111111").assetId(equipment1.getId()).build());
        final Inventory inventory1 = inventoryRepository.save(InventoryFixture.create(equipment1, member.getId()));

        // when, then
        RestAssured.given(requestSpec)
                .filter(document("deleteInventory"))
                .sessionId(sessionId)
                .when().log().all().delete("/api/inventories/" + inventory1.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("특정 담은 기자재를 수정한다.")
    void updateInventory() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("aaaaaaaa").build());
        itemRepository.save(ItemFixture.builder().propertyNumber("11111111").assetId(equipment1.getId()).build());
        final Inventory inventory1 = inventoryRepository.save(InventoryFixture.create(equipment1, member.getId()));
        final UpdateInventoryRequest request = UpdateInventoryRequest.builder()
                .rentalStartDate(LocalDate.now().plusDays(2))
                .rentalEndDate(LocalDate.now().plusDays(3))
                .amount(1)
                .build();

        // when, then
        RestAssured.given(requestSpec)
                .filter(document("updateInventory"))
                .sessionId(sessionId)
                .body(request).contentType(ContentType.JSON)
                .when().log().all().patch("/api/inventories/" + inventory1.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
