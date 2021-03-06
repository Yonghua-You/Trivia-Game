package com.campsite.reservation.service;

import com.campsite.reservation.domain.Reservation;
import com.campsite.reservation.repo.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);
    private ReservationRepository reservationRepository;
    @Autowired
    public ReservationService(ReservationRepository repository)
    {
        this.reservationRepository = repository;
    }
    @Transactional(readOnly = true)
    public List<LocalDate> searchVacantDays(LocalDate startDate, LocalDate endDate)
    {
        LOGGER.info("search vacancny during {} and {}", startDate, endDate);
        LocalDate now = LocalDate.now();
        if(!startDate.isAfter(now) || !endDate.isAfter(now))
        {
           throw new IllegalArgumentException("Start date or end date is illegal before now ");
        }
        if(startDate.isEqual(endDate) || startDate.isAfter(endDate)){
            throw new IllegalArgumentException("End date must be equal to start date or greater than start date");
        }

        List<LocalDate> vacantDays = Stream.iterate(startDate, date->date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .collect(Collectors.toList());
        List<Reservation> reservations = reservationRepository.searchForDateRange(startDate, endDate);

        reservations.forEach(b -> vacantDays.removeAll(b.getRevervationDates()));
        return vacantDays;

    }

    @Transactional(readOnly = true)
    public Reservation searchReservationByGuid(UUID guid)
    {
        LOGGER.info("finding the reservation for {}", guid);
        Optional<Reservation> reservation = reservationRepository.searchByGuid(guid);
        if (!reservation.isPresent()) {
            throw new ReservationNotFoundException(String.format("guid=%s not existing reservation", guid));
        }
        return reservation.get();
    }
    @Transactional()
    @Retryable(include = CannotAcquireLockException.class,
            maxAttempts = 2, backoff=@Backoff(delay = 150, maxDelay = 300))
    public Reservation save(Reservation reservation)
    {
        LOGGER.info("Create a new reservation for {}: start date: {} ; end date: {}", reservation.getGuid(), reservation.getStartDate(), reservation.getEndDate());
        if (!reservation.isNew()) {
            throw new IllegalReservationStateException("This is not new reservation");
        }
        List<LocalDate> vacantDays = searchVacantDays(reservation.getStartDate(), reservation.getEndDate());

        if (!vacantDays.containsAll(reservation.getRevervationDates())) {
            String message = String.format("No vacant dates available from %s to %s",
                    reservation.getStartDate(), reservation.getEndDate());
            throw new ReservationDatesNotAvailableException(message);
        }
        reservation.setCanceled(false);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public  Reservation updateReservation(Reservation reservation)
    {
        LOGGER.info("Update an existing reservation for {}: start date: {} ; end date: {}", reservation.getGuid(), reservation.getStartDate(), reservation.getEndDate());
        Reservation persistedReservation = searchReservationByGuid(reservation.getGuid());

        if (persistedReservation.isCanceled()) {
            String message = String.format("Reservation with guid=%s is cancelled", reservation.getGuid());
            throw new IllegalReservationStateException(message);
        }
        List<LocalDate> vacantDays = searchVacantDays(reservation.getStartDate(), reservation.getEndDate());
        vacantDays.addAll(persistedReservation.getRevervationDates());

        if (!vacantDays.containsAll(reservation.getRevervationDates())) {
            String message = String.format("No vacant dates available from %s to %s",
                    reservation.getStartDate(), reservation.getEndDate());
            throw new ReservationDatesNotAvailableException(message);
        }
//        reservation.setCanceled(false);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public boolean cancel(UUID guid)
    {
        Reservation reservation = searchReservationByGuid(guid);
        LOGGER.info("Cancel the reservation for {}: start date: {} ; end date: {}", reservation.getGuid(), reservation.getStartDate(), reservation.getEndDate());
        reservation.setCanceled(true);
        reservation = reservationRepository.save(reservation);
        return reservation.isCanceled();
    }
}
