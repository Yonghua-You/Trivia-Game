package com.game.trivia.service;
import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Game;
import com.game.trivia.domain.Question;
import com.game.trivia.repo.QuestionRepository;
import com.game.trivia.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class QuestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    public QuestionService(QuestionRepository repository)
    {
        this.questionRepository = repository;
    }

    @Transactional(readOnly = true)
    public Question findQuestionById(Long id)
    {
        LOGGER.info("finding the question for {}", id);
        Optional<Question> question = questionRepository.searchById(id);
        if (!question.isPresent()) {
            throw new NotFoundException(String.format("id=%s not existing question", id));
        }
        return question.get();
    }
    @Transactional
    @Retryable(include = CannotAcquireLockException.class,
            maxAttempts = 2, backoff=@Backoff(delay = 150, maxDelay = 300))
    public Question save(Question question)
    {
        LOGGER.info("Create a new question {} for game {}: ", question.getId(), question.getGame().getFullName());
        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(Question question)
    {
        LOGGER.info("Update an existing quesiton {} for game {}", question.getId(), question.getGame().getGuid());
        return questionRepository.save(question);
    }

    @Transactional
    public void delete(Question question)
    {
        LOGGER.info("delete the question {} for game {}", question.getId(), question.getGame().getGuid());
        questionRepository.delete(question);

    }
    @Transactional
    public Boolean checkIsCorrectAnswer(Question question, Long answer_id) {
        if (!question.getIsValid() || question.getCorrectAnswer() == null) {
            return false;
        }

        return question.getCorrectAnswer().getId().equals(answer_id);
    }

    @Transactional
    public void setCorrectAnswer(Question question, Answer answer) {
        question.setCorrectAnswer(answer);
        save(question);
    }
    @Transactional
    public Answer addAnswerToQuestion(Answer answer, Question question) {
        Answer newAnswer = updateAndSaveAnswer(answer, question);
        return newAnswer;
    }
    @Transactional
    public List<Question> findQuestionsByGame(Game game) {
        return questionRepository.findQuestionByGameOrderByOrderAsc(game);
    }

    @Transactional
    public List<Question> findValidQuestionsByGame(Game game) {
        return questionRepository.findQuestionByGameAndIsValidTrueOrderByOrderAsc(game);
    }

    private Answer updateAndSaveAnswer(Answer answer, Question question) {
        answer.setQuestion(question);
        return answerService.save(answer);
    }



}
