package com.girigiri.kwrental.asset.labroom.domain;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class LabRoomDailyBan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate banDate;

	@Column(nullable = false)
	private Long labRoomId;

	protected LabRoomDailyBan() {
	}

	@Builder
	private LabRoomDailyBan(Long id, LocalDate banDate, Long labRoomId) {
		this.id = id;
		this.banDate = banDate;
		this.labRoomId = labRoomId;
	}

	public boolean hasAny(Set<LocalDate> dates) {
		return dates.contains(banDate);
	}
}
