package com.girigiri.kwrental.labroom.domain;

import java.time.LocalDate;

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
}
