package com.girigiri.kwrental.asset.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.asset.domain.RentableAsset;

public interface AssetRepository extends Repository<RentableAsset, Long>, AssetRepositoryCustom {
    Optional<RentableAsset> findById(Long id);

    RentableAsset save(RentableAsset rentableAsset);
}
