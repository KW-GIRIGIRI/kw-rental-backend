package com.girigiri.kwrental.operation.repository;

public interface EntireOperationRepositoryCustom {
	long updateEntireOperation(boolean isRunning);

	boolean exists();
}
