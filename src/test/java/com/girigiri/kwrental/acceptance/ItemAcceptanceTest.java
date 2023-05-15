package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemHistoriesResponse;
import com.girigiri.kwrental.item.dto.response.ItemHistory;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import io.restassured.RestAssured;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ItemAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private RentalSpecRepository rentalSpecRepository;
    @Autowired
    private ReservationSpecRepository reservationSpecRepository;

    @Test
    @DisplayName("기자재 품목 조회 API")
    void getItemsByEquipment() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().equipmentId(equipment.getId()).build();
        final Item item2 = ItemFixture.builder().equipmentId(equipment.getId()).propertyNumber("13579").build();
        final Item item3 = ItemFixture.builder().equipmentId(equipment.getId() + 1).propertyNumber("24680").build();
        itemRepository.saveAll(List.of(item1, item2, item3));

        // when
        final ItemsResponse response = RestAssured.given(requestSpec)
                .filter(document("getItemsByEquipment"))
                .when().get("/api/items?equipmentId=" + equipment.getId())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .and().extract().as(ItemsResponse.class);

        // then
        assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(ItemResponse.from(item1), ItemResponse.from(item2));
    }

    @Test
    @DisplayName("품목 조회 API")
    void getItem() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().equipmentId(equipment.getId()).build();
        final Item item2 = ItemFixture.builder().equipmentId(equipment.getId() + 1).propertyNumber("1346778").build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        final ItemResponse response = RestAssured.given(requestSpec)
                .filter(document("getItem"))
                .when().get("/api/items/" + item1.getId())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .and().extract().as(ItemResponse.class);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(ItemResponse.from(item1));
    }

    @Test
    @DisplayName("관리자 품목 대여 가능 상태 변경 API")
    void updateRentalAvailable() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().equipmentId(equipment.getId()).build();
        itemRepository.save(item1);
        final ItemRentalAvailableRequest requestBody = new ItemRentalAvailableRequest(false);

        // when
        RestAssured.given(requestSpec)
                .filter(document("admin_updateRentalAvailable"))
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(requestBody)
                .when().log().all().patch("/api/admin/items/" + item1.getId() + "/rentalAvailable")
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("관리자 품목 자산번호 변경 API")
    void updatePropertyNumber() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().equipmentId(equipment.getId()).build();
        itemRepository.save(item1);
        final ItemPropertyNumberRequest requestBody = new ItemPropertyNumberRequest("updatedNumber");

        // when
        RestAssured.given(requestSpec)
                .filter(document("admin_updatePropertyNumber"))
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(requestBody)
                .when().log().all().patch("/api/admin/items/" + item1.getId() + "/propertyNumber")
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("관리자 품목 삭제 API")
    void deleteItem() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = ItemFixture.builder().equipmentId(equipment.getId()).build();
        itemRepository.save(item);

        // when
        RestAssured.given(requestSpec)
                .filter(document("admin_deleteItem"))
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when().log().all().delete("/api/admin/items/" + item.getId())
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("관리자가 기자재의 품목들 수정 API")
    void updateByEquipment() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = ItemFixture.builder().equipmentId(equipment.getId()).build();
        itemRepository.save(item);
        final Item item2 = ItemFixture.builder().propertyNumber("22222222").equipmentId(equipment.getId()).build();
        itemRepository.save(item2);

        UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(item.getId(), "11111111");
        UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(null, "33333333");
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(List.of(updateItemRequest1, updateItemRequest2));

        // when
        RestAssured.given(requestSpec)
                .filter(document("admin_updateItemsByEquipment"))
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(updateItemsRequest)
                .when().log().all().put("/api/admin/items?equipmentId=" + equipment.getId())
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value())
                .header(HttpHeaders.LOCATION, containsString("/api/items?equipmentId=" + equipment.getId()));
    }

    @Test
    @DisplayName("관리자가 현재 수령 가능한 품목 조회 API")
    void getAcceptableItems() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item.ItemBuilder itemBuilder = ItemFixture.builder().equipmentId(equipment.getId());
        final Item item1 = itemRepository.save(itemBuilder.propertyNumber("111111111").build());
        final Item item2 = itemRepository.save(itemBuilder.propertyNumber("222222222").build());
        final ReservationSpec reservationSpec = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).build());
        rentalSpecRepository.saveAll(List.of(
                RentalSpecFixture.builder().reservationSpecId(reservationSpec.getId()).propertyNumber(item1.getPropertyNumber()).acceptDateTime(LocalDateTime.now()).build()));

        // when
        final ItemsResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getAcceptableItems"))
                .when().log().all().get("/api/admin/items/rentalAvailability?equipmentId={equipmentId}", equipment.getId())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ItemsResponse.class);

        assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsOnly(ItemResponse.from(item2));

    }

    @Test
    @DisplayName("품목의 히스토리를 조회한다.")
    void getHistories() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item.ItemBuilder itemBuilder = ItemFixture.builder().equipmentId(equipment.getId());
        final Item item1 = itemRepository.save(itemBuilder.propertyNumber("111111111").build());
        final Item item2 = itemRepository.save(itemBuilder.propertyNumber("222222222").build());
        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec1 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(new RentalPeriod(now.minusDays(1), now)).build());
        final ReservationSpec reservationSpec2 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(new RentalPeriod(now.minusDays(2), now.minusDays(2))).build());
        final ReservationSpec reservationSpec3 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(new RentalPeriod(now.minusDays(2), now.minusDays(2))).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().reservationSpecId(reservationSpec1.getId()).propertyNumber(item1.getPropertyNumber()).status(RentalSpecStatus.RETURNED).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().reservationSpecId(reservationSpec2.getId()).propertyNumber(item2.getPropertyNumber()).status(RentalSpecStatus.LOST).build();
        final RentalSpec rentalSpec3 = RentalSpecFixture.builder().reservationSpecId(reservationSpec3.getId()).propertyNumber(item1.getPropertyNumber()).status(RentalSpecStatus.LOST).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

        // when
        final ItemHistoriesResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getItemHistories"))
                .when().log().all().get("/api/admin/items/histories?size=2&from={from}&to={to}", now.minusDays(2).toString(), now.toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ItemHistoriesResponse.class);

        // then
        assertAll(
                () -> assertThat(response.getHistories()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(new ItemHistory(Category.CAMERA, equipment.getModelName(), item2.getPropertyNumber(), 0, 1),
                                new ItemHistory(Category.CAMERA, equipment.getModelName(), item1.getPropertyNumber(), 1, 1)),
                () -> assertThat(response.getPage()).isEqualTo(0),
                () -> assertThat(response.getEndpoints()).hasSize(1)
                        .containsExactly("/api/admin/items/histories?from=2023-05-13&to=2023-05-15&size=2&page=0&sort=id,DESC")
        );
    }
}
