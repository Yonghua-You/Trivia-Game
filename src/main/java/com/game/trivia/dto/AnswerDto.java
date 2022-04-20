package com.game.trivia.dto;

import com.fasterxml.jackson.annotation.*;
import com.game.trivia.domain.Question;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.game.trivia.domain.validator.GameValidStartDateTime;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.Default;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = AnswerDto.class)
public class AnswerDto {
    private Long id;

    @NotEmpty
    @Size(max = 50)
    private String text;

    @Future(message = "the number of player who select the answer")
    private Integer playerCount;

    @ManyToOne
    private Integer order;

    @NotNull
    @Future(message = "create date must be between game start and end date")
    private LocalDateTime createdDateTime;

    public AnswerDto() {

    }


}
