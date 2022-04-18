package com.game.trivia.repo;
import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Game;
import com.game.trivia.domain.Question;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface AnswerRepository extends CrudRepository<Answer, Long>{
    @Query("select r from Answer r where r.id = ?1")
    Optional<Answer> searchById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    List<Answer> findAnswersByQuestion(Question question);
}
