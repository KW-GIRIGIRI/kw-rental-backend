package com.girigiri.kwrental.asset.service;

import com.girigiri.kwrental.asset.Rentable;
import com.girigiri.kwrental.equipment.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.equipment.dto.response.RemainQuantityPerDateResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetService {

    public RemainQuantitiesPerDateResponse getReservableCountPerDate(final Map<LocalDate, Integer> reservedAmounts, final Rentable rentable) {
        final List<RemainQuantityPerDateResponse> remainQuantityPerDateResponses = reservedAmounts.keySet().stream()
                .map(date -> new RemainQuantityPerDateResponse(date, rentable.getTotalQuantity() - reservedAmounts.get(date)))
                .sorted(Comparator.comparing(RemainQuantityPerDateResponse::getDate))
                .collect(Collectors.toList());
        return new RemainQuantitiesPerDateResponse(remainQuantityPerDateResponses);
    }
}
