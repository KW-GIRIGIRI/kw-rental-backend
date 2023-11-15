package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.exception.PropertyNumberNotUniqueException;
import com.girigiri.kwrental.item.repository.jpa.ItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemConstraintPolicyImpl implements ItemConstraintPolicy {

   private final ItemJpaRepository itemJpaRepository;

    @Override
    public void validateNotDeletedPropertyNumberIsUnique(final List<String> propertyNumbers) {
        if (propertyNumbers == null || propertyNumbers.isEmpty()) return;
        validateInputPropertyNumbersAreUnique(propertyNumbers);
        validateSavedPropertyNumbersNotExists(propertyNumbers);
    }

    private void validateSavedPropertyNumbersNotExists(final List<String> propertyNumbers) {
        final List<Item> foundItems = itemJpaRepository.findByPropertyNumbers(propertyNumbers);
        if (!foundItems.isEmpty()) {
            final List<String> duplicatedPropertyNumbers = foundItems.stream().map(Item::getPropertyNumber).toList();
            throw new PropertyNumberNotUniqueException(duplicatedPropertyNumbers);
        }
    }

    private void validateInputPropertyNumbersAreUnique(final List<String> propertyNumbers) {
        Set<String> uniquePropertyNumbers = new HashSet<>();
        Set<String> duplicatedPropertyNumbers = new HashSet<>();
        for (final String propertyNumber : propertyNumbers) {
            if (uniquePropertyNumbers.contains(propertyNumber)) {
                duplicatedPropertyNumbers.add(propertyNumber);
            } else {
                uniquePropertyNumbers.add(propertyNumber);
            }
        }
        if (!duplicatedPropertyNumbers.isEmpty()) throw new PropertyNumberNotUniqueException(duplicatedPropertyNumbers);
    }
}
