package com.girigiri.kwrental.inventory.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

// TODO: 2023/04/05 회원 관련 코드가 필요하다
@Entity
@Getter
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RentalDates rentalDates;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Long equipmentId;

    protected Inventory() {
    }

    public Inventory(final Long id, final RentalDates rentalDates, final Integer amount, final Long equipmentId) {
        this.id = id;
        this.rentalDates = rentalDates;
        this.amount = amount;
        this.equipmentId = equipmentId;
    }
}
