package com.game.trivia.dto;
import com.fasterxml.jackson.annotation.*;
import com.game.trivia.domain.Question;
import com.game.trivia.domain.validator.GameValidStartDateTime;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@GameValidStartDateTime
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = GameDto.class)
public class GameDto {
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Getter
    @EqualsAndHashCode.Include
    @Type(type = "uuid-char")
    @Column(name = "guid", nullable = false, unique = true)
    private UUID guid;

    @NotNull
    @Future(message = "Game start date and time must be before the end date and time")
    private LocalDateTime startDateTime;

    @NotNull
    @Future(message = "Game end date and time must be after the start date and time")
    private LocalDateTime endDateTime;

    private boolean isCanceled;
    @OneToMany
    @JsonProperty("questions")
    private List<QuestionDto> questions;

    public GameDto() {

    }


}
