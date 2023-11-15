package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.item.service.save.ItemSaverPerEquipmentImpl;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRetriever itemRetriever;
	@Mock
	private ItemAvailableSetter itemAvailableSetter;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ItemSaverPerEquipmentImpl itemSaver;
	@Mock
	private RentedItemService rentedItemService;
	@Mock
	private ItemDeleter itemDeleter;
	@InjectMocks
	private ItemService itemService;


}
