package com.hylanda.web.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface AnnoService {
	Class<?> value() default NullInterface.class;
	boolean singleton() default true;
}
