package com.campsite.reservation.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ReservationStartDateAheadEndDateValidator.class})
@Documented
public @interface ReservationStartDateAheadEndDate {
    String message() default "Reservation start date must be ahead of the end date";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
