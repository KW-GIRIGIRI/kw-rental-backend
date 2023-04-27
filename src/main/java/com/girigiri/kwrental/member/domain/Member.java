package com.girigiri.kwrental.member.domain;

import jakarta.persistence.*;
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
    private Member(final Long id, final String name, final String birthDate, final String memberNumber, final String password, final String email, final String phoneNumber, final Role role) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.memberNumber = memberNumber;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
