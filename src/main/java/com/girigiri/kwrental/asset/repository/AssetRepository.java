package com.girigiri.kwrental.asset.repository;

import com.girigiri.kwrental.asset.RentableAsset;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AssetRepository extends Repository<RentableAsset, Long> {
    Optional<RentableAsset> findByName(String name);

    Optional<RentableAsset> findById(Long id);

    RentableAsset save(RentableAsset rentableAsset);
}
