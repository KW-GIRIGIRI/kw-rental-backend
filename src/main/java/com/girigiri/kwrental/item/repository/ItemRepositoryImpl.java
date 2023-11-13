package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final ItemJpaRepository itemJpaRepository;
    private final ItemConstraintPolicy itemConstraintPolicy;

    @Override
    public int saveAll(List<Item> items) {
        itemConstraintPolicy.validateNotDeletedPropertyNumberIsUnique(items.stream().map(Item::getPropertyNumber).toList());
        return itemJpaRepository.saveAll(items);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemJpaRepository.findById(id);
    }

    @Override
    public Item save(Item item) {
        itemConstraintPolicy.validateNotDeletedPropertyNumberIsUnique(List.of(item.getPropertyNumber()));
        return itemJpaRepository.save(item);
    }

    @Override
    public Optional<Item> findByPropertyNumber(String propertyNumber) {
        return itemJpaRepository.findByPropertyNumber(propertyNumber);
    }

    @Override
    public int countAvailable(Long equipmentId) {
        return itemJpaRepository.countAvailable(equipmentId);
    }

    @Override
    public List<Item> findByEquipmentIds(Set<Long> equipmentIds) {
        return itemJpaRepository.findByEquipmentIds(equipmentIds);
    }

    @Override
    public int deleteByPropertyNumbers(List<String> propertyNumbers) {
        return itemJpaRepository.deleteByPropertyNumbers(propertyNumbers);
    }

    @Override
    public Page<EquipmentItemDto> findEquipmentItem(Pageable pageable, Category category) {
        return itemJpaRepository.findEquipmentItem(pageable, category);
    }

    @Override
    public int updateAvailable(List<Long> ids, boolean available) {
        return itemJpaRepository.updateAvailable(ids, available);
    }

    @Override
    public int deleteByIdIn(Collection<Long> ids) {
        return itemJpaRepository.deleteByIdIn(ids);
    }

    @Override
    public int deleteById(Long id) {
        return itemJpaRepository.deleteById(id);
    }

    @Override
    public List<Item> findByAssetId(Long assetId) {
        return itemJpaRepository.findByAssetId(assetId);
    }

    @Override
    public List<Item> findByPropertyNumbers(List<String> propertyNumbers) {
        return itemJpaRepository.findByPropertyNumbers(propertyNumbers);
    }
}
