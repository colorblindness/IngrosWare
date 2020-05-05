package me.xenforu.kelo.module.annotation;

import me.xenforu.kelo.module.ModuleCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleManifest {
    String label();
    ModuleCategory category();
    int color() default 0;
    int bind() default 0;
    boolean hidden() default false;
}
