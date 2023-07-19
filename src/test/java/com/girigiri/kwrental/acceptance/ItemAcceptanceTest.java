package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.LocalDate;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
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
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import io.restassured.RestAssured;

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
		final Item item1 = ItemFixture.builder().assetId(equipment.getId()).build();
		final Item item2 = ItemFixture.builder().assetId(equipment.getId()).propertyNumber("13579").build();
		final Item item3 = ItemFixture.builder().assetId(equipment.getId() + 1).propertyNumber("24680").build();
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
		final Item item1 = ItemFixture.builder().assetId(equipment.getId()).build();
		final Item item2 = ItemFixture.builder().assetId(equipment.getId() + 1).propertyNumber("1346778").build();
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
		final Equipment equipment = equipmentRepository.save(
			EquipmentFixture.builder().totalQuantity(1).rentableQuantity(1).build());
		final Item item1 = ItemFixture.builder().assetId(equipment.getId()).build();
		itemRepository.save(item1);
		final ItemRentalAvailableRequest requestBody = new ItemRentalAvailableRequest(false);

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_updateRentalAvailable"))
			.contentType(ContentType.APPLICATION_JSON.getMimeType())
			.body(requestBody)
			.when().log().all().patch("/api/admin/items/" + item1.getId() + "/rentalAvailable")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		Equipment actual = equipmentRepository.findById(equipment.getId()).orElseThrow();
		assertThat(actual.getRentableQuantity()).isEqualTo(0);
	}

	@Test
	@DisplayName("관리자 품목 자산번호 변경 API")
	void updatePropertyNumber() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final Item item1 = ItemFixture.builder().assetId(equipment.getId()).build();
		itemRepository.save(item1);
		EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().propertyNumber(
			item1.getPropertyNumber()).build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));
		final ItemPropertyNumberRequest requestBody = new ItemPropertyNumberRequest("updatedNumber");

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_updatePropertyNumber"))
			.contentType(ContentType.APPLICATION_JSON.getMimeType())
			.body(requestBody)
			.when().log().all().patch("/api/admin/items/" + item1.getId() + "/propertyNumber")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		EquipmentRentalSpec actual = rentalSpecRepository.findById(rentalSpec.getId())
			.orElseThrow()
			.as(EquipmentRentalSpec.class);
		assertThat(actual.getPropertyNumber()).isEqualTo(requestBody.propertyNumber());
	}

	@Test
	@DisplayName("관리자 품목 삭제 API")
	void deleteItem() {
		// given
		final Equipment equipment = equipmentRepository.save(
			EquipmentFixture.builder().rentableQuantity(0).totalQuantity(1).build());
		final Item item = ItemFixture.builder().assetId(equipment.getId()).available(false).build();
		itemRepository.save(item);

		EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().status(RentalSpecStatus.RETURNED)
			.propertyNumber(item.getPropertyNumber()).build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_deleteItem"))
			.contentType(ContentType.APPLICATION_JSON.getMimeType())
			.when().log().all().delete("/api/admin/items/" + item.getId())
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		Equipment actualEquipment = equipmentRepository.findById(equipment.getId()).orElseThrow();
		assertThat(actualEquipment.getTotalQuantity()).isZero();
		assertThat(actualEquipment.getRentableQuantity()).isZero();

		Item actualItem = itemRepository.findById(item.getId()).orElseThrow();
		assertThat(actualItem.getDeletedAt()).isNotNull();
		assertThat(actualItem.isAvailable()).isFalse();
	}

	@Test
	@DisplayName("관리자가 기자재의 품목들 수정 API")
	void updateByEquipment() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final Item item = ItemFixture.builder().assetId(equipment.getId()).build();
		itemRepository.save(item);
		final Item item2 = ItemFixture.builder().propertyNumber("22222222").assetId(equipment.getId()).build();
		itemRepository.save(item2);

		UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(item.getId(), "11111111");
		UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(null, "33333333");
		SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(
			List.of(updateItemRequest1, updateItemRequest2));

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
		final Item.ItemBuilder itemBuilder = ItemFixture.builder().assetId(equipment.getId());
		final Item item1 = itemRepository.save(itemBuilder.propertyNumber("111111111").build());
		final Item item2 = itemRepository.save(itemBuilder.propertyNumber("222222222").build());
		final ReservationSpec reservationSpec = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment).build());
		rentalSpecRepository.saveAll(List.of(
			EquipmentRentalSpecFixture.builder()
				.reservationSpecId(reservationSpec.getId())
				.propertyNumber(item1.getPropertyNumber())
				.build()));

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
		final Item.ItemBuilder itemBuilder = ItemFixture.builder().assetId(equipment.getId());
		final Item item1 = itemRepository.save(itemBuilder.propertyNumber("111111111").build());
		final Item item2 = itemRepository.save(itemBuilder.propertyNumber("222222222").build());
		final LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment).period(new RentalPeriod(now.minusDays(1), now)).build());
		final ReservationSpec reservationSpec2 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment)
				.period(new RentalPeriod(now.minusDays(2), now.minusDays(1)))
				.build());
		final ReservationSpec reservationSpec3 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment)
				.period(new RentalPeriod(now.minusDays(3), now.minusDays(2)))
				.build());
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.propertyNumber(item1.getPropertyNumber())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.propertyNumber(item2.getPropertyNumber())
			.status(RentalSpecStatus.LOST)
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec3.getId())
			.propertyNumber(item1.getPropertyNumber())
			.status(RentalSpecStatus.LOST)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

		// when
		final LocalDate from = now.minusDays(3);
		final ItemHistoriesResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getItemHistories"))
			.when().log().all()
			.get("/api/admin/items/histories?size=2&from={from}&to={to}", from.toString(), now.toString())
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(ItemHistoriesResponse.class);

		// then
		assertAll(
			() -> assertThat(response.getHistories()).usingRecursiveFieldByFieldElementComparator()
				.containsExactly(new ItemHistory(Category.CAMERA, equipment.getName(), item2.getPropertyNumber(), 0, 1),
					new ItemHistory(Category.CAMERA, equipment.getName(), item1.getPropertyNumber(), 1, 1)),
			() -> assertThat(response.getPage()).isEqualTo(0),
			() -> assertThat(response.getEndpoints()).hasSize(1)
				.containsExactly(
					String.format("/api/admin/items/histories?from=%s&to=%s&size=2&page=0&sort=id,DESC", from, now))
		);
	}
}
