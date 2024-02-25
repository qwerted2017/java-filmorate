package ru.yandex.practicum.filmorate.annotations;

import javax.validation.Constraint;
import javax.validation.constraints.Past;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Past
@Constraint(validatedBy = MinimumDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumDate {
    String message() default "Date should be later {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "1895-12-28";
}
