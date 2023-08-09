package com.girigiri.kwrental.item.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record ItemHistoriesResponse(

	List<ItemHistory> histories,
	Integer page,
	List<String> endpoints) {

	public static ItemHistoriesResponse of(final Page<ItemHistory> page, final List<String> allPageEndPoints) {
		return new ItemHistoriesResponse(page.getContent(), page.getNumber(), allPageEndPoints);
	}
}
