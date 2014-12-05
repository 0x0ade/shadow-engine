package net.fourbytes.shadow.map;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a field in a GameObject or a class extending GameObject as saveable.
 * It's content won't get lost upon conversion in {@link ShadowMap}.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsSaveable {
    public boolean value() default true;
}
