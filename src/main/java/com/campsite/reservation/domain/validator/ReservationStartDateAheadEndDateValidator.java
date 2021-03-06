package com.campsite.reservation.domain.validator;

import com.campsite.reservation.domain.Reservation;
import com.campsite.reservation.web.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReservationStartDateAheadEndDateValidator implements ConstraintValidator<ReservationStartDateAheadEndDate, ReservationDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationMaxDaysValidator.class);
    @Override
    public void initialize(ReservationStartDateAheadEndDate constraintAnnotation) {
        // no additional initialization needed
    }

    @Override
    public boolean isValid(ReservationDto reservation, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.debug("Start before end date Validation: Start date: " +  reservation.getStartDate().toString() + " End date: "  + reservation.getEndDate().toString());
        return reservation.getStartDate().isBefore(reservation.getEndDate());
    }
}
