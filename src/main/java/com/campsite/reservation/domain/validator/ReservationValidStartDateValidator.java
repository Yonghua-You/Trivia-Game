package com.campsite.reservation.domain.validator;

import com.campsite.reservation.web.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReservationValidStartDateValidator implements ConstraintValidator<ReservationValidStartDate, ReservationDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationMaxDaysValidator.class);
    @Override
    public void initialize(ReservationValidStartDate constraintAnnotation) {
        // no additional initialization needed
    }

    @Override
    public boolean isValid(ReservationDto reservation, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.debug("Start Date Validation: Start date: " +  reservation.getStartDate().toString() + " Current date: "  + LocalDate.now().toString());
        return LocalDate.now().isBefore(reservation.getStartDate())
                && reservation.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
    }
}
