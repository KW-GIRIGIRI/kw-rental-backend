package com.girigiri.kwrental.common.exception;

public class KwangwoonServerException extends InternalServerErrorException {
	public KwangwoonServerException() {
		super("광운대학교 서버에 문제가 생겼습니다.");
	}
}
