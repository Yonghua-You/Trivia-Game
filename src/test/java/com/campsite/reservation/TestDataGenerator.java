package com.campsite.reservation;

import com.campsite.reservation.web.ReservationDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class TestDataGenerator {
    public ReservationDto buildReservation(LocalDate startDate, LocalDate endDate) {
        return buildReservation(UUID.randomUUID(), "tester", "tester@sample.com", startDate, endDate);
    }

    public ReservationDto buildReservation(UUID guid, LocalDate startDate, LocalDate endDate) {
        return buildReservation(guid, "tester", "tester@sample.com", startDate, endDate);
    }

    public ReservationDto buildReservation(
            UUID uuid, String fullName, String email, LocalDate startDate, LocalDate endDate) {
        return ReservationDto.builder()
                .guid(uuid)
                .fullName(fullName)
                .email(email)
                .startDate(startDate)
                .endDate(endDate)
                .isCanceled(false)
                .build();
    }
}
