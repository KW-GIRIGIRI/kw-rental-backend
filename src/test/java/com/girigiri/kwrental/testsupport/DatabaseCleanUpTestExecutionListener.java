package com.girigiri.kwrental.testsupport;

import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@Component
@Order(Integer.MAX_VALUE)
public class DatabaseCleanUpTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Override
    public void beforeTestMethod(final TestContext testContext) {
        initBeans(testContext);
        databaseCleanUp.execute();
    }

    private void initBeans(final TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
    }
}
