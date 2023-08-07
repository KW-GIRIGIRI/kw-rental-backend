package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;

public interface RentalSpecCreator<T> {
	List<RentalSpec> create(T rentalSpecRequest);
}
