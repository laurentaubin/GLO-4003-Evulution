package ca.ulaval.glo4003.ws.api.filters.allowed;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Allowed {
  String[] roles() default {"BASE"};
}
