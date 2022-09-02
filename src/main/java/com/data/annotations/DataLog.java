package com.data.annotations;


import com.data.enums.TemplateEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataLog {

    String fileName() default "data_log.log";

    TemplateEnum Template();

}
