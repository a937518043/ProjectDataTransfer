package com.data.annotations;


import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyField {

    String name();

    String type();
}
