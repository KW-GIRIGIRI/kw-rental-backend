package com.girigiri.kwrental.common;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

	@Override
	public LocalTime deserialize(final JsonParser jsonParser,
		final DeserializationContext deserializationContext) throws IOException {
		String timeStr = jsonParser.getValueAsString();
		return LocalTime.parse(timeStr, formatter);
	}
}
