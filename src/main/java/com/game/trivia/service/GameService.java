package com.game.trivia.service;

import com.game.trivia.domain.Game;
import com.game.trivia.domain.Question;
import com.game.trivia.domain.Response;
import com.game.trivia.domain.Result;
import com.game.trivia.repo.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private GameRepository gameRepository;
    @Autowired
    private QuestionService questionService;

    @Autowired
    public GameService(GameRepository repository)
    {
        this.gameRepository = repository;
    }

    @Transactional(readOnly = true)
    public List<Game> searchFutureGames(LocalDateTime startDateTime)
    {
        LOGGER.info("finding future games!");
        return gameRepository.searchFutureGames(startDateTime);
    }

    @Transactional(readOnly = true)
    public Game searchGameByGuid(UUID guid)
    {
        LOGGER.info("finding the game for {}", guid);
        Optional<Game> game = gameRepository.searchByGuid(guid);
        if (!game.isPresent()) {
            throw new NotFoundException(String.format("guid=%s not existing game", guid));
        }
        return game.get();
    }

    @Transactional()
    @Retryable(include = CannotAcquireLockException.class,
            maxAttempts = 2, backoff=@Backoff(delay = 150, maxDelay = 300))
    public Game save(Game game)
    {
        LOGGER.info("Create a new game for {}: start date: {} ; end date: {}", game.getGuid(), game.getStartDateTime(), game.getEndDateTime());
        game.setCanceled(false);
        game.getQuestions().stream().forEach(question-> {
                question.setGame(game);
                question.getAnswers().stream().forEach(answer -> {
                    answer.setQuestion(question);
                });
            });
        return gameRepository.save(game);
    }

    @Transactional
    public Game updateGame(Game game)
    {
        LOGGER.info("Update an existing game for {}: start date: {} ; end date: {}", game.getGuid(), game.getStartDateTime(), game.getEndDateTime());
        Game persistedGame = searchGameByGuid(game.getGuid());

        if (persistedGame.isCanceled()) {
            String message = String.format("Game with guid=%s is cancelled", game.getGuid());
            throw new IllegalStateException(message);
        }
        return gameRepository.save(game);
    }

    @Transactional
    public boolean cancel(UUID guid)
    {
        Game game = searchGameByGuid(guid);
        LOGGER.info("Cancel the game for {}: start date: {} ; end date: {}", game.getGuid(), game.getStartDateTime(), game.getEndDateTime());
        game.setCanceled(true);
        game = gameRepository.save(game);
        return game.isCanceled();
    }
    @Transactional
    public Result checkAnswer(Game game, List<Response> answersBundles) {
        Result results = new Result();

        for (Question question : game.getQuestions()) {
            boolean isFound = false;

            if (!question.getIsValid()) {
                continue;
            }
            Optional<Response> answer = answersBundles.stream().filter(a->a.getQuestion().equals(question.getId())).findFirst();
            if (answer.isPresent()) {
                isFound = true;
                results.addAnswer(questionService.checkIsCorrectAnswer(question, answer.get().getSelectedAnswer()));
                break;
            }

            if (!isFound) {
                throw new NotFoundException("No answer found for question: " + question.getText());
            }
        }

        return results;
    }
}
