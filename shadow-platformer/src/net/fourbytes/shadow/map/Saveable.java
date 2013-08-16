package net.fourbytes.shadow.map;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies an field in an {@link GameObject} as saveable. 
 * It's content won't get lost upon conversion in {@link ShadowMap}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Saveable {

}
