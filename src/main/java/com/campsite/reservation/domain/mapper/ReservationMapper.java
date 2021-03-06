package com.campsite.reservation.domain.mapper;

import com.campsite.reservation.domain.Reservation;
import com.campsite.reservation.web.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);
    Reservation toReservation(ReservationDto reservationDto);
    ReservationDto toReservationDto(Reservation reservation);
}
