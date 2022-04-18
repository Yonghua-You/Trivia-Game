package com.game.trivia.service;

import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Question;
import com.game.trivia.repo.AnswerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class AnswerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnswerService.class);
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionService questionService;

    @Autowired
    public AnswerService(AnswerRepository repository)
    {
        this.answerRepository = repository;
    }

    @Transactional(readOnly = true)
    public Answer findAnswerById(Long id)
    {
        LOGGER.info("finding the answer for {}", id);
        Optional<Answer> question = answerRepository.searchById(id);
        if (!question.isPresent()) {
            throw new NotFoundException(String.format("id=%s not existing question", id));
        }
        return question.get();
    }

    @Transactional
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer update(Answer newAnswer){
        Answer currentAnswer = findAnswerById(newAnswer.getId());
        return answerRepository.save(currentAnswer);
    }

    @Transactional
    public void delete(Answer answer) {

//        if (questionService.checkIsCorrectAnswer(answer.getQuestion(), answer.getId())) {
//            throw new IllegalStateException("The correct answer can't be deleted");
//        }

        answerRepository.delete(answer);
    }

    @Transactional
    public List<Answer> findAnswersByQuestion(Question question) {
        return answerRepository.findAnswersByQuestion(question);
    }
}
