package com.campsite.reservation.web;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import com.campsite.reservation.TestDataGenerator;
import com.campsite.reservation.repo.ReservationRepository;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationControllerTest {
    @Autowired
    private ReservationRepository repository;

    @Autowired
    private TestDataGenerator generator;

    @LocalServerPort
    int port;

    private String controllerPath = "/reservation";
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        repository.deleteAll();
    }

    @Test
    void getVacantDates() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        // when
        List<String> vacantDates = given()
                .param("startDate", startDate.toString()).param("endDate", endDate.toString())
                .when().get(controllerPath + "/vacantdays")
                .then().extract().body().as(List.class);
        // then
        List<String> expected = Stream.iterate(startDate, date->date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .map(String::valueOf)
                .collect(Collectors.toList());
        assertThat(vacantDates).isEqualTo(expected);
    }

    @Test
    void get() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(2);
        UUID guid = UUID.randomUUID();

        ReservationDto newReservation = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildReservation(guid, startDate, endDate))
                .when().post(controllerPath)
                .as(ReservationDto.class);
        // when
        ReservationDto foundReservation = given().pathParam("guid", guid)
                .when().get(controllerPath + "/{guid}")
                .as(ReservationDto.class);

        // then
        assertThat(foundReservation).isEqualTo(newReservation);
    }

    @Test
    void create() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        // when
        int code = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildReservation(startDate, endDate))
                .when().post(controllerPath)
                .thenReturn()
                .statusCode();
        // then
        assertThat(code).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void update() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(1);
        UUID guid = UUID.randomUUID();

        ReservationDto newReservation = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildReservation(guid, startDate, endDate))
                .when().post(controllerPath)
                .as(ReservationDto.class);
        newReservation.setEndDate(endDate.plusDays(1));
        // when
        ReservationDto updatedreservation = given()
                .pathParam("guid", guid)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(newReservation)
                .when().put(controllerPath + "/{guid}")
                .as(ReservationDto.class);
        // then
        assertThat(updatedreservation.getGuid()).isEqualTo(newReservation.getGuid());
        assertThat(updatedreservation.getVersion()).isEqualTo(newReservation.getVersion() + 1);
        assertThat(updatedreservation.getEndDate()).isEqualTo(endDate.plusDays(1));
    }

    @Test
    void cancel() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(6);
        LocalDate endDate = LocalDate.now().plusDays(7);
        UUID guid = UUID.randomUUID();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generator.buildReservation(guid, startDate, endDate))
                .when().post(controllerPath)
                .as(ReservationDto.class);
        // when
        ValidatableResponse response = given().pathParam("guid", guid)
                .when().delete(controllerPath + "/{guid}").then();
        // then
        response.assertThat().statusCode(HttpStatus.OK.value());
    }
}