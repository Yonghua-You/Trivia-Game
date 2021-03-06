package com.campsite.reservation.domain.validator;

import com.campsite.reservation.web.ReservationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationMaxDaysValidator implements ConstraintValidator<ReservationMaxDays, ReservationDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationMaxDaysValidator.class);
    @Override
    public void initialize(ReservationMaxDays constraintAnnotation) {
        // no additional initialization needed
    }

    @Override
    public boolean isValid(ReservationDto reservation, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.info("Max Days Validation: Start date: " +  reservation.getStartDate().toString() + " End date: "  + reservation.getEndDate().toString());
        return Period.between(reservation.getStartDate(), reservation.getEndDate()).getDays() <= 3;
    }
}
