package com.game.trivia;

import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Question;
import com.game.trivia.dto.GameDto;
import com.game.trivia.dto.QuestionDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TestDataGenerator {
    public GameDto buildGame(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return buildGame(UUID.randomUUID(), startDateTime, endDateTime);
    }

    public GameDto buildGame(UUID guid, LocalDateTime startDate, LocalDateTime endDate) {
        Question question = new Question();
        Answer answer = new Answer();
        Answer answer1 = new Answer();
        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        answers.add(answer1);
        question.setAnswers(answers);
        List<QuestionDto> questions = new ArrayList<>();
        return buildGame(guid, startDate, endDate, questions);
    }

    public GameDto buildGame(
            UUID uuid, LocalDateTime startDateTime, LocalDateTime endDateTime, List<QuestionDto> questions) {
        return GameDto.builder()
                .guid(uuid)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .isCanceled(false)
                .questions(questions)
                .build();
    }

}
