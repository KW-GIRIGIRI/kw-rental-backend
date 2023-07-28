package com.girigiri.kwrental.aws.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class FileNameEmptyException extends BadRequestException {
	public FileNameEmptyException() {
		super("파일이름 없습니다.");
	}
}
