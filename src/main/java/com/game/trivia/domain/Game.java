package com.game.trivia.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "game")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Getter
    @EqualsAndHashCode.Include
    @Type(type = "uuid-char")
    @Column(name = "guid", nullable = false, unique = true)
    private UUID guid;

    @Column(name = "fullName", nullable = true, length = 100)
    private String fullName;

    @Column(name = "startDateTime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "endDateTime", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "iscanceled", nullable = false)
    private boolean isCanceled;

    @OneToMany(mappedBy = "game",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @OneToMany
    private List<Question> questions;




}
