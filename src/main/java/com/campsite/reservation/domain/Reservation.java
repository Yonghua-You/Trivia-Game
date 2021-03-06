package com.campsite.reservation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "Reservation")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Getter
    @EqualsAndHashCode.Include
    @Type(type = "uuid-char")
    @Column(name = "guid", nullable = false, unique = true)
    private UUID guid;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "fullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "iscanceled", nullable = false)
    private boolean isCanceled;

    public boolean isNew() {
        return this.id == null;
    }

    public List<LocalDate> getRevervationDates() {
        return Stream.iterate(startDate, date->date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .collect(Collectors.toList());
    }

}
