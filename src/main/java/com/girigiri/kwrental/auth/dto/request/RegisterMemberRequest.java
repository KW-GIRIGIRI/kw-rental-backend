package com.girigiri.kwrental.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class RegisterMemberRequest {
    @Length(min = 2)
    private String name;

    @Length(min = 6, max = 6)
    private String birthDate;

    @NotEmpty
    private String memberNumber;

    @Length(min = 8)
    private String password;

    @Email
    private String email;

    @NotEmpty
    private String phoneNumber;

    private RegisterMemberRequest() {
    }

    @Builder
    private RegisterMemberRequest(final String name, final String birthDate, final String memberNumber, final String password, final String email, final String phoneNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.memberNumber = memberNumber;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
