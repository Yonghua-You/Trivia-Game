package com.game.trivia.domain.validator;

import com.game.trivia.dto.GameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class GameValidStartDateTimeValidator implements ConstraintValidator<GameValidStartDateTime, GameDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameValidStartDateTimeValidator.class);
    @Override
    public void initialize(GameValidStartDateTime constraintAnnotation) {
        // no additional initialization needed
    }

    @Override
    public boolean isValid(GameDto gameDto, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.debug("Start Date and time of the game: " +  gameDto.getStartDateTime().toString() + " Current date and time: "  + LocalDateTime.now().toString());
        return LocalDateTime.now().isBefore(gameDto.getStartDateTime())
                && gameDto.getStartDateTime().isBefore(LocalDateTime.now().plusDays(1));
    }
}
