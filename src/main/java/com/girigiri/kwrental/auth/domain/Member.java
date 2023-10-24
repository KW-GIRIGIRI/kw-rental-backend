package com.girigiri.kwrental.auth.domain;

import com.girigiri.kwrental.auth.exception.MemberException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String birthDate;

	@Column(nullable = false)
	private String memberNumber;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String phoneNumber;

	@Enumerated(value = EnumType.STRING)
	private Role role;

	protected Member() {
	}

	@Builder
	private Member(final Long id, final String name, final String birthDate, final String memberNumber,
		final String password, final String email, final String phoneNumber, final Role role) {
		validateName(name);
		validateBirthDate(birthDate);
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.memberNumber = memberNumber;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	private void validateName(final String name) {
		if (name == null || name.length() < 2) {
			throw new MemberException("이름 데이터가 없거나 2글자 미만 입니다.");
		}
	}

	private void validateBirthDate(final String birthDate) {
		if (birthDate == null || birthDate.length() != 6) {
			throw new MemberException("생년월일 데이터가 존재하지 않거나 6글자가 아닙니다.");
		}
	}

	public void updatePassword(final String password) {
		this.password = password;
	}

	public void updateEmail(final String email) {
		this.email = email;
	}

	public void updatePhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isAdmin() {
		return this.role == Role.ADMIN;
	}

	public boolean hasSameEmail(final String email) {
		return this.email.equals(email);
	}
}
