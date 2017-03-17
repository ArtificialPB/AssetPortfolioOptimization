package com.artificial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
public @interface NumberData {
    Period value() default Period.MONTHLY;

    NumberType type() default NumberType.PERCENT;
}
