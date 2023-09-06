package com.girigiri.kwrental.schedule.repository;

public interface EntireOperationRepositoryCustom {
	long updateEntireOperation(boolean isRunning);

	boolean exists();
}
