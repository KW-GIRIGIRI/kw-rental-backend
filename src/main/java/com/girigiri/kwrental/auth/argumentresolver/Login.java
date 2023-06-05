package com.girigiri.kwrental.auth.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.girigiri.kwrental.auth.domain.Role;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {

	@AliasFor("role")
	Role[] value() default {Role.USER};

	@AliasFor("value")
	Role[] role() default {Role.USER};
}
