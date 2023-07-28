package com.girigiri.kwrental.rental.service.rent.validator;

public interface RentValidator<T> {
	void validate(T rentalRequest);
}
