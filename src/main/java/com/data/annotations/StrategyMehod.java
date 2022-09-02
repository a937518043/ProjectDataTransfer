package com.data.annotations;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyMehod {

    String StrategyExecute() default "";

    boolean close() default true;
}
