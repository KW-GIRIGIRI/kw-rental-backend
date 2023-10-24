package com.girigiri.kwrental.asset.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class RentableAssetException extends BadRequestException {
	public RentableAssetException(String message) {
		super(message);
	}
}
