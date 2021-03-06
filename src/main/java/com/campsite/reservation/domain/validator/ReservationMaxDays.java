package com.campsite.reservation.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ReservationMaxDaysValidator.class})
@Documented
public @interface ReservationMaxDays {
    String message() default "Reservation max days are three days";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
