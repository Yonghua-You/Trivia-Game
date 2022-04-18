package com.game.trivia.repo;

import com.game.trivia.domain.Game;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, Long> {
    @Query("select r from Game r where r.guid = ?1")
    Optional<Game> searchByGuid(UUID guid);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    @Query("select b from Game b "
            + "where ((?1 <= b.startDateTime and ?1 < b.endDateTime) )"
            + "order by b.startDateTime asc ")
    List<Game> searchFutureGames(LocalDateTime startDateTime);

}
