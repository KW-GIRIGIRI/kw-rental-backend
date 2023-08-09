package com.girigiri.kwrental.asset.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.exception.AssetNotFoundException;
import com.girigiri.kwrental.asset.repository.AssetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class AssetService {

    private final AssetRepository assetRepository;

    public RemainQuantitiesPerDateResponse getReservableCountPerDate(final Map<LocalDate, Integer> reservedAmounts,
        final RentableAsset asset) {
        final List<RemainQuantityPerDateResponse> remainQuantityPerDateResponses = reservedAmounts.keySet().stream()
            .map(date -> new RemainQuantityPerDateResponse(date, asset.getRemainQuantity(reservedAmounts.get(date))))
            .sorted(Comparator.comparing(RemainQuantityPerDateResponse::date))
            .toList();
        return new RemainQuantitiesPerDateResponse(remainQuantityPerDateResponses);
    }

    public RentableAsset getAssetById(final Long id) {
        return assetRepository.findById(id)
            .orElseThrow(AssetNotFoundException::new);
    }
}
