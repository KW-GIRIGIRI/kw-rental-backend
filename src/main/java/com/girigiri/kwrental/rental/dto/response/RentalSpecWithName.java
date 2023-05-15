package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RentalSpecWithName {

    private final String name;
    private final LocalDateTime acceptDateTime;
    private final LocalDateTime returnDateTime;
    private final RentalSpecStatus status;

    public RentalSpecWithName(final String name, final RentalDateTime acceptDateTime, final RentalDateTime returnDateTime, final RentalSpecStatus status) {
        this(name, acceptDateTime == null ? null : acceptDateTime.toLocalDateTime(), returnDateTime == null ? null : returnDateTime.toLocalDateTime(), status);
    }

    private RentalSpecWithName(final String name, final LocalDateTime acceptDateTime, final LocalDateTime returnDateTime, final RentalSpecStatus status) {
        this.name = name;
        this.acceptDateTime = acceptDateTime;
        this.returnDateTime = returnDateTime;
        this.status = status;
    }
}
