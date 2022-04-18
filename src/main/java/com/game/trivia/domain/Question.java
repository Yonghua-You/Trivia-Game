package com.game.trivia.domain;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "question")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "text", nullable = false, length = 150)
    private String text;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
//    @ManyToOne
    @JoinColumn(name="game_id", nullable = false)
    //@JsonBackReference
    //@JsonIgnore
    private Game game;

    @Column(name = "q_order")
    private Integer order;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JsonManagedReference
//    @OneToMany
    private List<Answer> answers;

    @OneToOne
    private Answer correctAnswer;

    @Column(name = "create_datetime", nullable = false)
    private LocalDateTime createdDateTime;

    private Boolean isValid = false;




}
