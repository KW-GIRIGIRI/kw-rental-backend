package com.girigiri.kwrental.member.repository;

import com.girigiri.kwrental.member.domain.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByMemberNumber(String memberNumber);
}