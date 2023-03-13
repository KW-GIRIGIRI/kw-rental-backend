package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import com.girigiri.kwrental.equipment.EquipmentRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.EquipmentsPageResponse;
import com.girigiri.kwrental.support.DatabaseCleanUp;
import com.girigiri.kwrental.support.ResetDatabaseTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
class EquipmentAcceptanceTest extends ResetDatabaseTest {

    @LocalServerPort
    private int port;

    private RequestSpecification requestSpec;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RestAssured.requestSpecification = this.requestSpec;
        this.requestSpec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withResponseDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
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
        final EquipmentDetailResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipment"))
                .when().get("/api/equipments/{id}", equipment.getId())
                .then().statusCode(HttpStatus.OK.value()).log().all()
                .and().extract().as(EquipmentDetailResponse.class);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(TestFixtures.createEquipmentDetailResponse());
    }

    @Test
    @DisplayName("기자재 목록 조회 API")
    void getEquipmentsPage() {
        // given
        final Equipment equipment1 = TestFixtures.createEquipment();
        equipmentRepository.save(equipment1);
        final Equipment equipment2 = TestFixtures.createEquipment();
        equipmentRepository.save(equipment2);
        final Equipment equipment3 = TestFixtures.createEquipment();
        equipmentRepository.save(equipment3);
        final Equipment equipment4 = TestFixtures.createEquipment();
        equipmentRepository.save(equipment4);

        // when
        final EquipmentsPageResponse response = RestAssured.given(this.requestSpec)
                .filter(document("getEquipmentsPage"))
                .when().get("/api/equipments?size=2")
                .then().statusCode(HttpStatus.OK.value()).log().all()
                .and().extract().as(EquipmentsPageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.endPoints()).hasSize(2)
                        .containsExactly("/api/equipments?size=2&page=0&sort=id,DESC",
                                "/api/equipments?size=2&page=1&sort=id,DESC"),
                () -> assertThat(response.items()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(TestFixtures.createEquipmentResponse(), TestFixtures.createEquipmentResponse())
        );
    }
}
