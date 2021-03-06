package com.campsite.reservation.service;

public class ReservationDatesNotAvailableException extends RuntimeException{
    public ReservationDatesNotAvailableException(String message) {
        super(message);
    }
}
