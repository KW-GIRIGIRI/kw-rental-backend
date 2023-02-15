package com.girigiri.kwrental.equipment;

import com.girigiri.kwrental.equipment.exception.EquipmentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@Entity
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String maker;

    @Column(nullable = false)
    private String modelName;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remainingQuantity;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private LocalTime availableTimeFrom;

    @Column(nullable = false)
    private LocalTime availableTimeTo;

    @Column(nullable = false)
    private String description;

    protected Equipment() {
    }

    public Equipment(final Long id, final Category category, final String maker, final String modelName,
                     final int totalQuantity, final int remainingQuantity,
                     final String imgUrl, final LocalTime availableTimeFrom, final LocalTime availableTimeTo,
                     final String description) {
        validateQuantity(totalQuantity, remainingQuantity);
        validateAvailableTime(availableTimeFrom, availableTimeTo);
        this.id = id;
        this.category = category;
        this.maker = maker;
        this.modelName = modelName;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.imgUrl = imgUrl;
        this.availableTimeFrom = availableTimeFrom;
        this.availableTimeTo = availableTimeTo;
        this.description = description;
    }

    private static void validateQuantity(final int totalQuantity, final int remainingQuantity) {
        if (remainingQuantity < 0) {
            throw new EquipmentException("남은 갯수가 음수일 수 없습니다.");
        }
        if (totalQuantity < 0) {
            throw new EquipmentException("전체 갯수가 음수일 수 없습니다.");
        }
        if (totalQuantity < remainingQuantity) {
            throw new EquipmentException("전체 갯수가 남은 갯수보다 적으면 안됩니다.");
        }
    }

    private static void validateAvailableTime(final LocalTime availableTimeFrom, final LocalTime availableTimeTo) {
        if (availableTimeFrom != null && availableTimeTo != null && availableTimeFrom.isAfter(availableTimeTo)) {
            throw new EquipmentException("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안됩니다.");
        }
    }
}
