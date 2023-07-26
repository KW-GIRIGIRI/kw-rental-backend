package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;

import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;

public interface RentalSpecCreator<T> {
	List<AbstractRentalSpec> create(T rentalSpecRequest);
}
