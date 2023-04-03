package com.girigiri.kwrental.equipment.controller;

import com.amazonaws.services.kms.model.AWSKMSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest.AddEquipmentRequestBuilder;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEquipmentController.class)
class AdminEquipmentControllerTest {

    private static final String PREFIX = "/api/admin/equipments";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @MockBean
    private MultiPartFileHandler multiPartFileHandler;

    final List<AddItemRequest> addItemRequests = List.of(
            new AddItemRequest("propertyNumber"), new AddItemRequest(null));
    private final ObjectMapper objectMapper = new ObjectMapper();
    AddEquipmentRequestBuilder addEquipmentRequestBuilder = AddEquipmentRequest.builder()
            .rentalPlace("rentalDays")
            .maker("maker")
            .modelName("modelName")
            .category("CAMERA")
            .description("description")
            .components("component")
            .purpose("purpose")
            .imgUrl("imgUrl")
            .maxRentalDays(1)
            .totalQuantity(2);

    @Test
    @DisplayName("Pageable의 size가 양수가 아닌 경우 10으로 변환된다.")
    void getEquipments_200_sizeNotPositive() throws Exception {
        // given
        long size = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipments(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get(PREFIX + "?size=" + size))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Pageable의 page가 음수인 경우 0으로 변환된다.")
    void getEquipments_200_pageNotPositive() throws Exception {
        // given
        long page = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipments(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get(PREFIX + "?page=" + page))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색어가 양끝단 공백제외 2글자 이내 예외처리.")
    void getEquipments_400_trimmedValueOver200() throws Exception {
        // given
        String keyword = "  h  ";

        // when, then
        mockMvc.perform(get(PREFIX + "?keyword=" + keyword))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 카테고리가 잘못된 경우 예외처리.")
    void getEquipments_400_categoryNotMatch() throws Exception {
        // given
        String category = "notExistsCategory";

        // when, then
        mockMvc.perform(get(PREFIX + "?category=" + category))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 정렬기준이 잘못된 경우 예외처리.")
    void getEquipments_400_sortNotMatch() throws Exception {
        // given
        String sort = "notExistsSort";
        given(equipmentService.findEquipments(any(), any())).willThrow(
                TransientDataAccessResourceException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "?sort=" + sort))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 이름이 없는 경우 예외처리")
    void saveEquipment_400_nameIsEmpty() throws Exception {
        // given
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .modelName(null).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 제조사가 없는 경우 예외처리")
    void saveEquipment_400_makerIsEmpty() throws Exception {
        // given
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .maker(null).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 구성품 문자열 길이가 200 초과 시 예외처리")
    void saveEquipment_400_componentOver200() throws Exception {
        // given
        String components = "a".repeat(201);
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .components(components).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 목적 문자열 길이가 100 초과 시 예외처리")
    void saveEquipment_400_purposeOver100() throws Exception {
        // given
        String purpose = "a".repeat(101);
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .purpose(purpose).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 대여장소가 없을 시 예외처리")
    void saveEquipment_400_rentalPlaceIsNull() throws Exception {
        // given
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .rentalPlace(null).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 최대 대여 일 수가 양수가 아닌 경우 예외처리")
    void saveEquipment_400_maxRentalDaysNegative() throws Exception {
        // given
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .maxRentalDays(0).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 안내사항 문자열 길이가 500 초과 시 예외처리")
    void saveEquipment_400_descriptionOver500() throws Exception {
        // given
        String description = "a".repeat(501);
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .description(description).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재 추가 시 총 갯수와 품목 갯수가 맞지 않을 시 예외처리")
    void saveEquipment_400_totalQuantityNotMatch() throws Exception {
        // given
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder
                .totalQuantity(1).build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("데이터베이스 제약조건(unique 조건)을 위배한 경우 예외 처리")
    void saveEquipment_duplicatedModelName() throws Exception {
        // given
        given(equipmentService.saveEquipment(any())).willThrow(DataIntegrityViolationException.class);
        final AddEquipmentRequest addEquipmentRequest = addEquipmentRequestBuilder.build();
        final String requestBody = createAddEquipmentAndDefaultItemsRequestBody(
                addEquipmentRequest);

        // when, then
        mockMvc.perform(post(PREFIX).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이미지 업로드 중 AWS 예외가 발생한 경우 예외 처리")
    void uploadImage_AmazonServiceException() throws Exception {
        given(multiPartFileHandler.upload(any())).willThrow(AWSKMSException.class);

        // when, then
        mockMvc.perform(post(PREFIX + "/images").contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("존재하지 않는 기자재 삭제하려는 예외 처리")
    void deleteEquipment_notFound() throws Exception {
        doThrow(EquipmentNotFoundException.class).when(equipmentService).deleteEquipment(1L);

        // when, then
        mockMvc.perform(delete(PREFIX + "/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private String createAddEquipmentAndDefaultItemsRequestBody(final AddEquipmentRequest addEquipmentRequest)
            throws JsonProcessingException {
        final AddEquipmentWithItemsRequest addEquipmentWithItemsRequest = new AddEquipmentWithItemsRequest(
                addEquipmentRequest, addItemRequests);
        return objectMapper.writeValueAsString(addEquipmentWithItemsRequest);
    }
}
