package com.girigiri.kwrental.testsupport;

import org.mockito.ArgumentMatcher;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;

public class DeepReflectionEqMatcher<T> implements ArgumentMatcher<T> {

    private final T expect;
    private final String[] ignoreFields;

    public DeepReflectionEqMatcher(final T expect, final String... ignoreFields) {
        this.expect = expect;
        this.ignoreFields = ignoreFields;
    }

    public static <T> T deepRefEq(final T value, final String... ignoreFields) {
        return argThat(new DeepReflectionEqMatcher<>(value, ignoreFields));
    }

    @Override
    public boolean matches(final T argument) {
        if (Collection.class.isAssignableFrom(argument.getClass())) {
            return checkCollection((Collection<?>) argument);

        }
        return checkNonCollection(argument);
    }

    private boolean checkCollection(final Collection<?> argument) {
        try {
            assertThat(argument).usingRecursiveFieldByFieldElementComparatorIgnoringFields(ignoreFields)
                    .isEqualTo(expect);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkNonCollection(final T argument) {
        try {
            assertThat(argument).usingRecursiveComparison()
                    .ignoringFields(ignoreFields)
                    .isEqualTo(expect);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
