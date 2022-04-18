package com.game.trivia.dto;
import com.fasterxml.jackson.annotation.*;
import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Game;
import com.game.trivia.domain.validator.GameValidStartDateTime;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = QuestionDto.class)
public class QuestionDto {
    private Long id;

    @NotEmpty
    @Size(max = 150)
    private String text;
    @ManyToOne

    private Integer order;

    @OneToMany
    @JsonProperty("answers")
    private List<AnswerDto> answers;

    @JsonIgnore
    @OneToOne
    private AnswerDto correctAnswer;

    @NotNull
    @Future(message = "create date must be between game start and end date")
    private LocalDateTime createdDateTime;

    private Boolean isValid = true;

    public QuestionDto() {

    }

}
