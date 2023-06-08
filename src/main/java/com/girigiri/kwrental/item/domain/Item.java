package com.girigiri.kwrental.item.domain;

import java.time.LocalDate;

import com.girigiri.kwrental.item.exception.ItemException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Item {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String propertyNumber;

    private boolean available = true;

    @Column(nullable = false)
    private Long assetId;

    private LocalDate deletedAt;

    protected Item() {
    }

    @Builder
    private Item(final Long id, final String propertyNumber, final boolean available, final Long assetId,
        final LocalDate deletedAt) {
        this.id = id;
        this.propertyNumber = propertyNumber;
        this.available = available;
        this.assetId = assetId;
        this.deletedAt = deletedAt;
    }

    public Item(final Long id, final String propertyNumber, final boolean available, final Long assetId) {
        this(id, propertyNumber, available, assetId, null);
    }

    public void updatePropertyNumber(String propertyNumber) {
        if (propertyNumber == null || propertyNumber.isBlank()) {
            throw new ItemException("자산 번호가 null이거나 빈 공백이면 안됩니다.");
        }
        this.propertyNumber = propertyNumber;
    }

    public void setAvailable(final boolean available) {
        this.available = available;
    }
}
