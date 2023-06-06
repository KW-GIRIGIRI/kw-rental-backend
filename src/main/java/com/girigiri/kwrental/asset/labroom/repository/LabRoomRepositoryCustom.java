package com.girigiri.kwrental.asset.labroom.repository;

public interface LabRoomRepositoryCustom {
	void updateNotice(Long id, String content);

	void updateAvailable(Long id, boolean available);
}
