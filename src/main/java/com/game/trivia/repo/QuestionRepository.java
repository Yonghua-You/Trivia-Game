package com.game.trivia.repo;
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
public interface QuestionRepository extends CrudRepository<Question, Long>{
    @Query("select r from Question r where r.id = ?1")
    Optional<Question> searchById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    List<Question> findQuestionByGame(Game game);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    List<Question> findQuestionByGameAndIsValidTrueOrderByOrderAsc(Game game);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    List<Question> findQuestionByGameOrderByOrderAsc(Game game);

}
