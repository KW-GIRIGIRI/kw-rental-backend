package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.response.*;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static com.girigiri.kwrental.equipment.domain.Category.CAMERA;
import static com.girigiri.kwrental.equipment.domain.Category.ETC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class EquipmentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private RentalSpecRepository rentalSpecRepository;

    @Test
    @DisplayName("기자재 세부 내역 조회 API")
    void getEquipment() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        equipmentRepository.save(equipment);

        // when
        final EquipmentDetailResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipment"))
                .when().get("/api/equipments/{id}", equipment.getId())
                .then().statusCode(HttpStatus.OK.value()).log().all()
                .and().extract().as(EquipmentDetailResponse.class);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(EquipmentDetailResponse.from(equipment));
    }

    @Test
    @DisplayName("기자재 목록 조회 API")
    void getEquipmentsPage() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().modelName("name1").build();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = EquipmentFixture.builder().modelName("name2").build();
        equipmentRepository.save(equipment2);
        final Equipment equipment3 = EquipmentFixture.builder().modelName("name3").build();
        equipmentRepository.save(equipment3);
        final Equipment equipment4 = EquipmentFixture.builder().modelName("name4").build();
        equipmentRepository.save(equipment4);

        // when
        final EquipmentsWithRentalQuantityPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipmentsPage"))
                .when().get("/api/equipments?size=2")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .and().extract().as(EquipmentsWithRentalQuantityPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(2)
                        .containsExactly("/api/equipments?size=2&page=0&sort=id,DESC",
                                "/api/equipments?size=2&page=1&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment4, equipment4.getTotalQuantity()),
                                SimpleEquipmentWithRentalQuantityResponse.from(equipment3, equipment3.getTotalQuantity()))
        );
    }

    @Test
    @DisplayName("기자재 목록 조회 API_모델명으로 검색")
    void getEquipmentsPage_search() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().modelName("key").build();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = EquipmentFixture.builder().modelName("akey").build();
        equipmentRepository.save(equipment2);
        final Equipment equipment3 = EquipmentFixture.builder().modelName("akeyb").build();
        equipmentRepository.save(equipment3);
        final Equipment equipment4 = EquipmentFixture.builder().modelName("keyb").build();
        equipmentRepository.save(equipment4);

        final Equipment equipment5 = EquipmentFixture.builder().modelName("notForSearch").build();
        equipmentRepository.save(equipment5);

        // when
        final EquipmentsWithRentalQuantityPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipmentsPageWithSearch"))
                .when().get("/api/equipments?size=2&keyword=key")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .and().extract().as(EquipmentsWithRentalQuantityPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(2)
                        .containsExactly("/api/equipments?keyword=key&size=2&page=0&sort=id,DESC",
                                "/api/equipments?keyword=key&size=2&page=1&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment4, equipment4.getTotalQuantity()),
                                SimpleEquipmentWithRentalQuantityResponse.from(equipment3, equipment3.getTotalQuantity()))
        );
    }

    @Test
    @DisplayName("기자재 목록 조회 API_카테고리로 필터링하여 검색")
    void getEquipmentsPage_searchWithCategory() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().modelName("key").category(CAMERA).build();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = EquipmentFixture.builder().modelName("akey").category(CAMERA).build();
        equipmentRepository.save(equipment2);
        final Equipment equipment3 = EquipmentFixture.builder().modelName("akeyb").category(CAMERA).build();
        equipmentRepository.save(equipment3);
        final Equipment equipment4 = EquipmentFixture.builder().modelName("keyb").category(ETC).build();
        equipmentRepository.save(equipment4);

        final Equipment equipment5 = EquipmentFixture.builder().modelName("notForSearch").build();
        equipmentRepository.save(equipment5);

        // when
        final EquipmentsWithRentalQuantityPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipmentsPageWithSearchAndCategory"))
                .when().get("/api/equipments?size=2&keyword=key&category=CAMERA")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .and().extract().as(EquipmentsWithRentalQuantityPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(2)
                        .containsExactly("/api/equipments?keyword=key&category=CAMERA&size=2&page=0&sort=id,DESC",
                                "/api/equipments?keyword=key&category=CAMERA&size=2&page=1&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment3, equipment3.getTotalQuantity()),
                                SimpleEquipmentWithRentalQuantityResponse.from(equipment2, equipment2.getTotalQuantity()))
        );
    }

    @Test
    @DisplayName("기자재 목록 조회 API_날짜에 따라 대여 가능한 갯수 다르게 보여짐.")
    void getEquipmentsPage_withDate() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().modelName("equipment1").totalQuantity(10).build();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = EquipmentFixture.builder().modelName("equipment2").totalQuantity(10).build();
        equipmentRepository.save(equipment2);
        LocalDate date = LocalDate.of(2023, 1, 1);
        rentalSpecRepository.save(ReservationSpecFixture.builder(equipment1).amount(new RentalAmount(5)).period(new RentalPeriod(date, date.plusDays(1))).build());
        rentalSpecRepository.save(ReservationSpecFixture.builder(equipment1).amount(new RentalAmount(2)).period(new RentalPeriod(date, date.plusDays(1))).build());
        rentalSpecRepository.save(ReservationSpecFixture.builder(equipment2).amount(new RentalAmount(5)).period(new RentalPeriod(date, date.plusDays(1))).build());

        // when
        final EquipmentsWithRentalQuantityPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipmentsPageWithDate"))
                .when().get("/api/equipments?size=2&date=2023-01-01")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .and().extract().as(EquipmentsWithRentalQuantityPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(1)
                        .containsExactly("/api/equipments?date=2023-01-01&size=2&page=0&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment2, 5),
                                SimpleEquipmentWithRentalQuantityResponse.from(equipment1, 3))
        );
    }

    @Test
    @DisplayName("관리자가 기자재 페이지 조회 API")
    void getEquipmentsPageAdmin() {
        final Equipment equipment1 = EquipmentFixture.builder().modelName("key").category(CAMERA).build();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = EquipmentFixture.builder().modelName("akey").category(CAMERA).build();
        equipmentRepository.save(equipment2);
        final Equipment equipment3 = EquipmentFixture.builder().modelName("akeyb").category(CAMERA).build();
        equipmentRepository.save(equipment3);
        final Equipment equipment4 = EquipmentFixture.builder().modelName("keyb").category(ETC).build();
        equipmentRepository.save(equipment4);

        final Equipment equipment5 = EquipmentFixture.builder().modelName("notForSearch").build();
        equipmentRepository.save(equipment5);

        // when
        final EquipmentPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("admin_getEquipmentPageWithSearchAndCategory"))
                .when().get("/api/admin/equipments?size=2&keyword=key&category=CAMERA")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .and().extract().as(EquipmentPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(2)
                        .containsExactly("/api/admin/equipments?keyword=key&category=CAMERA&size=2&page=0&sort=id,DESC",
                                "/api/admin/equipments?keyword=key&category=CAMERA&size=2&page=1&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(SimpleEquipmentResponse.from(equipment3),
                                SimpleEquipmentResponse.from(equipment2))
        );
    }

    @Test
    @DisplayName("관라지가 기자재 추가 API")
    void addEquipment() {
        // given
        final AddEquipmentRequest equipment = new AddEquipmentRequest(
                "rentalDays", "modelName",
                "CAMERA", "maker", "imgUrl",
                "component", "purpose", "description", 1, 2);
        final List<AddItemRequest> items = List.of(new AddItemRequest("12345678"), new AddItemRequest("8765421"));
        final AddEquipmentWithItemsRequest requestBody = new AddEquipmentWithItemsRequest(equipment, items);

        // when, then
        RestAssured.given(this.requestSpec)
                .filter(document("admin_addEquipment"))
                .body(requestBody).contentType(ContentType.JSON)
                .when().log().all().post("/api/admin/equipments")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/equipments/"));
    }

    @Test
    @DisplayName("관리자가 기자재 삭제 API")
    void deleteEquipment() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        equipmentRepository.save(equipment);

        // when
        RestAssured.given(this.requestSpec)
                .filter(document("admin_deleteEquipment"))
                .contentType(ContentType.JSON)
                .when().log().all().delete("/api/admin/equipments/" + equipment.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("관리자가 이미지 등록 API")
    void uploadEquipmentImage() throws IOException {
        // given
        given(amazonS3.getUrl(any(), any())).willReturn(new URL("http://localhost:8001/mock.png"));

        MockMultipartFile file = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());

        // when
        RestAssured.given(this.requestSpec)
                .filter(document("admin_uploadImage"))
                .contentType(ContentType.MULTIPART)
                .multiPart("file", "file.png", file.getInputStream())
                .when().log().all().post("/api/admin/equipments/images")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header(HttpHeaders.LOCATION, containsString(".png"));
    }

    @Test
    @DisplayName("관리자가 기자재 수정 API")
    void updateEquipmentAndItems() {
        // given
        Equipment equipment = equipmentRepository.save(EquipmentFixture.create());

        UpdateEquipmentRequest updateEquipmentRequest = new UpdateEquipmentRequest(
                "rentalDays", "modelName",
                "CAMERA", "maker", "imgUrl",
                "component", "purpose", "description", 1, 1);

        // when
        RestAssured.given(this.requestSpec)
                .filter(document("admin_updateEquipment"))
                .body(updateEquipmentRequest)
                .contentType(ContentType.JSON)
                .when().log().all().put("/api/admin/equipments/" + equipment.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header(HttpHeaders.LOCATION, containsString("/api/equipments/" + equipment.getId()));
    }
}