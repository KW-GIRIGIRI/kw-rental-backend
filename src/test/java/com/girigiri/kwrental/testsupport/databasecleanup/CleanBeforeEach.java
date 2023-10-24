package com.girigiri.kwrental.testsupport.databasecleanup;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(value = {DatabaseCleanUpTestExecutionListener.class}, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public @interface CleanBeforeEach {
}
