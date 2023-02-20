package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.girigiri.kwrental.equipment.Equipment;
import com.girigiri.kwrental.equipment.EquipmentRepository;
import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.support.DatabaseCleanUp;
import com.girigiri.kwrental.support.ResetDatabaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EquipmentAcceptanceTest extends ResetDatabaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Test
    @DisplayName("기자재 세부 내역 조회 API")
    void getEquipment() {
        // given
        final Equipment equipment = TestFixtures.createEquipment();
        equipmentRepository.save(equipment);

        // when
        final EquipmentDetailResponse response = RestAssured.given()
                .when().get("/api/equipments/{id}", equipment.getId())
                .then().statusCode(HttpStatus.OK.value()).log().all()
                .and().extract().as(EquipmentDetailResponse.class);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(TestFixtures.createEquipmentResponse());
    }
}
