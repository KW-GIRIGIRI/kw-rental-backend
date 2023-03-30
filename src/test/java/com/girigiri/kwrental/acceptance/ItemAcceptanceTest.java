package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class ItemAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("기자재 품목 조회 API")
    void getItemsByEquipment() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().equipmentId(equipment.getId()).build();
        final Item item2 = ItemFixture.builder().equipmentId(equipment.getId()).propertyNumber(null).build();
        final Item item3 = ItemFixture.builder().equipmentId(equipment.getId() + 1).propertyNumber(null).build();
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
        final Item item2 = ItemFixture.builder().equipmentId(equipment.getId() + 1).propertyNumber(null).build();
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
}