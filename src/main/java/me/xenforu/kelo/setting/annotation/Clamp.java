package me.xenforu.kelo.setting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Clamp {
    String minimum() default "0";
    String maximum() default "20";
    String inc() default "1";
}
