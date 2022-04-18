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
@Table(name = "answer")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 50)
    private String text;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
//    @ManyToOne
    @JoinColumn(name="question_id", nullable = false)
    private Question question;

    @Column(name = "a_order")
    private Integer order;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createdDateTime;

    @Column(name = "player")
    private String player;


    @Override
    public String toString() {
        return "Answer [text=" + text + ", question=" + question + ", order=" + order + ", createdDate=" + createdDateTime
                + "]";
    }
}
