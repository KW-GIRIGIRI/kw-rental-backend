package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class RentalSpecs {

    private final Map<Long, RentalSpec> rentalSpecMap;

    private RentalSpecs(final Map<Long, RentalSpec> rentalSpecMap) {
        this.rentalSpecMap = rentalSpecMap;
    }

    public static RentalSpecs from(final List<RentalSpec> rentalSpecs) {
        final Map<Long, RentalSpec> rentalSpecMap = rentalSpecs.stream()
                .collect(toMap(RentalSpec::getId, Function.identity()));
        return new RentalSpecs(rentalSpecMap);
    }

    public void setStatus(final Long id, final RentalStatus status) {
        final RentalSpec rentalSpec = rentalSpecMap.get(id);
        if (rentalSpec == null) throw new RentalSpecNotFoundException();
        rentalSpec.setStatus(status);
    }
}
