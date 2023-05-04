package com.girigiri.kwrental.rental.dto.request;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReturnRentalSpecRequest {
    @NotNull
    private Long id;
    @NotNull
    private RentalSpecStatus status;

    private ReturnRentalSpecRequest() {
    }

    private ReturnRentalSpecRequest(final Long id, final RentalSpecStatus status) {
        this.id = id;
        this.status = status;
    }
}
