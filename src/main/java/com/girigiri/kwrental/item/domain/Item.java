package com.girigiri.kwrental.item.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class Item {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String propertyNumber;

    @Builder.Default
    private boolean rentalAvailable = true;

    @Column(nullable = false)
    private Long equipmentId;

    protected Item() {
    }

    public Item(final Long id, final String propertyNumber, final boolean rentalAvailable, final Long equipmentId) {
        this.id = id;
        this.propertyNumber = propertyNumber;
        this.rentalAvailable = rentalAvailable;
        this.equipmentId = equipmentId;
    }
}
