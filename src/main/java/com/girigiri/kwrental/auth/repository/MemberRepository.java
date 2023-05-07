package com.girigiri.kwrental.auth.repository;

import com.girigiri.kwrental.auth.domain.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByMemberNumber(String memberNumber);

    Optional<Member> findById(Long id);
}