package com.game.trivia.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {GameValidStartDateTimeValidator.class})
@Documented
public @interface GameValidStartDateTime {
    String message() default "Game start date and time must be from future";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
