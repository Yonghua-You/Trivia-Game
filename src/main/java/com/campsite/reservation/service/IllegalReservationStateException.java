package com.campsite.reservation.service;

public class IllegalReservationStateException extends RuntimeException{
    public IllegalReservationStateException(String message) {
        super(message);
    }
}
