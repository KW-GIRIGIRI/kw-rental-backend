package com.girigiri.kwrental.asset.equipment.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class DuplicateAssetNameException extends DomainException {
	public DuplicateAssetNameException(final String name) {
		super(String.format("%s이라는 자산이 이미 존재하는 것 같습니다.", name));
	}
}
