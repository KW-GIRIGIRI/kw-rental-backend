package com.girigiri.kwrental.testsupport;

import com.girigiri.kwrental.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@DataJpaTest
@Import({JpaConfig.class, RepositoryTestConfig.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepositoryTest {
}
