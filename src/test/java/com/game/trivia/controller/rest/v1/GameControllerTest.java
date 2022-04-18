package com.game.trivia.controller.rest.v1;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.game.trivia.TestDataGenerator;
import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Game;
import com.game.trivia.domain.Question;
import com.game.trivia.dto.GameDto;
import com.game.trivia.repo.GameRepository;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {
    @Autowired
    private GameRepository repository;

    @Autowired
    private TestDataGenerator generator;

    @LocalServerPort
    int port;

    private String controllerPath = "/api/game";
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        repository.deleteAll();
    }

    @Test
    void findFutureGame() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(2);
        UUID guid = UUID.randomUUID();
        GameDto game = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildGame(guid, startDateTime, endDateTime))
                .when().post(controllerPath)
                .as(GameDto.class);

        // when
        LocalDateTime searchStartTime = LocalDateTime.now();
        Game game1 = given()
                .param("startDateTime", searchStartTime.toString())
                .when().get(controllerPath + "/futuregame")
                .then().extract().body().as(Game.class);
        // then
        assertThat(game).isEqualTo(game1);
    }

    @Test
    void findAllFutureGames()
    {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(2);
        UUID guid = UUID.randomUUID();
        GameDto game = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildGame(guid, startDateTime, endDateTime))
                .when().post(controllerPath)
                .as(GameDto.class);

        LocalDateTime startDateTime1 = LocalDateTime.now().plusHours(2);
        LocalDateTime endDateTime1 = LocalDateTime.now().plusHours(3);
        UUID guid1 = UUID.randomUUID();
        GameDto game1 = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildGame(guid1, startDateTime1, endDateTime1))
                .when().post(controllerPath)
                .as(GameDto.class);

        // when
        LocalDateTime searchStartTime = LocalDateTime.now();
        List<Game> games = given()
                .param("startDateTime", searchStartTime.toString())
                .when().get(controllerPath + "/futuregames")
                .then().extract().body().as(List.class);
        // then
        assertThat(games.size()).isEqualTo(2);
    }

    @Test
    void findNextQuestion() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(2);
        UUID guid = UUID.randomUUID();
        GameDto game = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildGame(guid, startDateTime, endDateTime))
                .when().post(controllerPath)
                .as(GameDto.class);
        // when
        Question foundQuestion = given().pathParam("guid", guid).pathParam("q_order", 0)
                .when().get(controllerPath + "/{guid}" + "/question" + "/{q_order}")
                .as(Question.class);

        // then
        assertThat(foundQuestion).isNotNull();
    }

    @Test
    void findAnswers(){
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(2);
        UUID guid = UUID.randomUUID();
        GameDto game = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildGame(guid, startDateTime, endDateTime))
                .when().post(controllerPath)
                .as(GameDto.class);
        Long questionId = game.getQuestions().get(0).getId();
        // when
        List<Answer> answers = given().pathParam("guid", guid).pathParam("q_order", 0)
                .when().get(controllerPath + "/{guid}" + "/question" + "/{q_order}" + "/answers")
                .as(List.class);

        // then
        assertThat(answers.size()).isEqualTo(2);
    }


}