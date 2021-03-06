package com.campsite.reservation.web;

import com.campsite.reservation.domain.validator.ReservationMaxDays;
import com.campsite.reservation.domain.validator.ReservationStartDateAheadEndDate;
import com.campsite.reservation.domain.validator.ReservationValidStartDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Version;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@ReservationMaxDays
@ReservationValidStartDate
@ReservationStartDateAheadEndDate
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
public class ReservationDto {
    private  Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @EqualsAndHashCode.Include
    @Column(name = "guid", nullable = false, unique = true)
    private UUID guid;

    @NotEmpty
    @Size(max = 100)
    private String email;

    @NotEmpty
    @Size(max = 100)
    private String fullName;

    @NotNull
    @Future(message = "reservation must start from future")
    private LocalDate startDate;

    @NotNull
    @Future(message = "Reservation must end at a future day")
    private  LocalDate endDate;

    private boolean isCanceled;

}
