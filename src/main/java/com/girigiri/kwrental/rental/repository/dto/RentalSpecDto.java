package com.girigiri.kwrental.rental.repository.dto;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import lombok.Getter;

import java.util.Objects;

@Getter

public class RentalSpecDto {

    private Long id;
    private String modelName;
    private RentalSpecStatus status;

    protected RentalSpecDto() {
    }

    public RentalSpecDto(final Long id, final String modelName, final RentalSpecStatus status) {
        this.id = id;
        this.modelName = modelName;
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RentalSpecDto that = (RentalSpecDto) o;
        return Objects.equals(id, that.id) && Objects.equals(modelName, that.modelName) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modelName, status);
    }
}
