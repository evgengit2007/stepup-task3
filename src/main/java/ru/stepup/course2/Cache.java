package ru.stepup.course2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    long value() default 0;
    // счетчик количества записей в кеше, после превышения которого запускается очистка
    int periodCheck() default 1000;
}
