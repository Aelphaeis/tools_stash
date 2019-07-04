package stash.directives;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import stash.transformer.Transformer;


@Target(TYPE)
@Retention(RUNTIME)
public @interface Supports {
	Class<? extends Transformer>[] value();
}
