package com.girigiri.kwrental.item.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class ItemRetriever {

	private final ItemRepository itemRepository;

	public Item getById(final Long id) {
		return itemRepository.findById(id)
			.orElseThrow(ItemNotFoundException::new);
	}

	public Item getByPropertyNumber(final String propertyNumber) {
		return itemRepository.findByPropertyNumber(propertyNumber)
			.orElseThrow(ItemNotFoundException::new);
	}

	public List<Item> getByIds(final List<Long> ids) {
		return itemRepository.findByIds(ids);
	}
}
