package com.campsite.reservation.repo;

import com.campsite.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    @Query("select r from Reservation r where r.guid = ?1")
    Optional<Reservation> searchByGuid(UUID guid);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    @Query("select b from Reservation b "
            + "where ((b.startDate < ?1 and ?2 < b.endDate) "
            + "or (?1 < b.endDate and b.endDate <= ?2) "
            + "or (?1 <= b.startDate and b.startDate <=?2)) "
            + "and b.isCanceled = false "
            + "order by b.startDate asc")
    List<Reservation> searchForDateRange(LocalDate startDate, LocalDate endDate);

}
