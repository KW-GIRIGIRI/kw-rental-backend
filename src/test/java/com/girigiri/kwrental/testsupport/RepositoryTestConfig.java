package com.girigiri.kwrental.testsupport;

import com.girigiri.kwrental.item.repository.ItemConstraintPolicy;
import com.girigiri.kwrental.item.repository.ItemConstraintPolicyImpl;
import com.girigiri.kwrental.item.repository.ItemJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RepositoryTestConfig {

    @Bean
    public ItemConstraintPolicy itemConstraintPolicy(ItemJpaRepository itemJpaRepository) {
        return new ItemConstraintPolicyImpl(itemJpaRepository);
    }
}
