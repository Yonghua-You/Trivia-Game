package com.game.trivia.controller.rest.v1;

import com.game.trivia.domain.*;
import com.game.trivia.domain.mapper.AnswerMapper;
import com.game.trivia.domain.mapper.GameMapper;
import com.game.trivia.domain.mapper.QuestionMapper;
import com.game.trivia.dto.AnswerDto;
import com.game.trivia.dto.GameDto;
import com.game.trivia.dto.QuestionDto;
import com.game.trivia.service.*;
import com.game.trivia.service.IllegalStateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Game API", description = "Game APIs")
@RequestMapping(GameController.ROOT_MAPPING)
public class GameController {
    public static final String ROOT_MAPPING = "/api/game";
    private GameService service;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    public GameController(GameService gameService)
    {
        this.service = gameService;
    }

    protected GameController()
    {

    }

    @Operation(summary = "search the next coming game", responses = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(value = "/futuregame", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GameDto> findFutureGame(
            @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime)
    {
        if (startDateTime == null) {
            startDateTime = LocalDateTime.now();
        }

        List<Game> futureGames;
        try {
            futureGames = service.searchFutureGames(startDateTime);
        }catch (IllegalArgumentException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }
        if(futureGames.size() == 0)
        {
            return new ResponseEntity(null, HttpStatus.OK);
        }else{
            return new ResponseEntity(getResource(futureGames.get(0)), HttpStatus.OK);
        }

    }

    @Operation(summary = "search future games", responses = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(value = "/futuregames", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GameDto>> findAllFutureGames(
            @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime)
    {
        if (startDateTime == null) {
            startDateTime = LocalDateTime.now();
        }

        List<Game> futureGames;
        try {
            futureGames = service.searchFutureGames(startDateTime);
        }catch (IllegalArgumentException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }
        List<GameDto> gameDtos = futureGames.stream().map(game->GameMapper.INSTANCE.toGameDto(game)).collect(Collectors.toList());
        return new ResponseEntity<>(gameDtos, HttpStatus.OK);
    }

    @Operation(summary = "get next question", responses = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(value = "/{guid}/question/{q_order}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<QuestionDto>  findNextQuestion(@PathVariable(name = "guid") String guid,
                                                  @PathVariable(name = "q_order") int currentQuestionOrder,
                                                  @RequestParam(name = "onlyValid", required = false, defaultValue = "false") Boolean onlyValid) {

        Game game = service.searchGameByGuid(UUID.fromString(guid));
        List<Question> questions;
        if (onlyValid) {
            questions = questionService.findValidQuestionsByGame(game);
        } else {
            questions = questionService.findQuestionsByGame(game);
        }
        if(currentQuestionOrder == 0 && questions.size() > 0)
        {
            return new ResponseEntity(EntityModel.of(QuestionMapper.INSTANCE.toQuestionDto(questions.get(0))), HttpStatus.OK);
        }
        for (int i = 0; i < questions.size(); i++)
        {
            if(questions.get(i).getOrder() == currentQuestionOrder && i + 1 != questions.size())
            {
                return new ResponseEntity(EntityModel.of(QuestionMapper.INSTANCE.toQuestionDto(questions.get(i + 1))), HttpStatus.OK);
            }
        }
        return new ResponseEntity(null, HttpStatus.OK);

    }

    @Operation(summary = "get answers for the question", responses = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(value = "/{guid}/question/{question_id}/answers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnswerDto>> findAnswers(@PathVariable(name = "guid") String guid,
            @PathVariable(name = "question_id") Long question_id) {
        Question question = questionService.findQuestionById(question_id);
        List<Answer> answers = answerService.findAnswersByQuestion(question);
        List<AnswerDto> answerDtos = answers.stream().map(answer -> AnswerMapper.INSTANCE.toAnswerDto(answer)).collect(Collectors.toList());
        return new ResponseEntity<>(answerDtos, HttpStatus.OK);
    }

    @Operation(summary = "Find the game", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @GetMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<GameDto>> get(@PathVariable(name = "guid") String guid)
    {
        Game game;
        try {
            game = service.searchGameByGuid(UUID.fromString(guid));
        }catch (NotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }
        return new ResponseEntity<>(getResource(game), HttpStatus.OK);
    }

    @Operation(summary = "Create new game", responses = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<GameDto>> create(@RequestBody() @Valid GameDto gameDto)
    {
        Game game;
        try {
            game = service.save(GameMapper.INSTANCE.toGame(gameDto));
        }catch(IllegalStateException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }
        EntityModel<GameDto> resource = getResource(game);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()));

        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }
    @Operation(summary = "Update the game", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @PutMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<GameDto>> update(@PathVariable("guid") String guid, @RequestBody @Valid GameDto gameDto)
    {
        Game game;
        try {
            game = service.updateGame(
                    GameMapper.INSTANCE.toGame(gameDto));
        }catch (IllegalStateException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }catch (NotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }

        EntityModel<GameDto> resource = getResource(game);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    @Operation(summary = "delete the game by cancel it", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @DeleteMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancel (@PathVariable("guid") String guid)
    {
        boolean cancelled = false;
        try {
            cancelled = service.cancel(UUID.fromString(guid));
        }catch (NotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }
        if (cancelled) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "submit an answer for a game", responses = {
            @ApiResponse(responseCode = "200", description = "submit successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(value = "/{guid}/question/{question_id}/submitAnswer",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Result> submitAnswer(HttpServletResponse response,
                        @PathVariable(name = "guid") String guid,
                        @PathVariable(name = "question_id") Long question_id,
                        @RequestBody List<Response> answersBundles) throws IOException
    {
        Game game = service.searchGameByGuid(UUID.fromString(guid));
        Result result = service.checkAnswer(game, answersBundles);
        answersBundles.stream().forEach(answer->{
            Answer theAnswer = answerService.findAnswerById(answer.getSelectedAnswer());
            Integer count = theAnswer.getPlayerCount() + 1;
            theAnswer.setPlayerCount(count);
            answerService.update(theAnswer);
        });
        if(result.getCorrectQuestions() == 0)
        {
//            httpSession.invalidate();
            httpSession.setAttribute("guid", guid);
            System.out.println(httpSession.getId());
            response.sendRedirect("/game/" + game.getGuid() +"/question/" + question_id +"/answers");
            return new ResponseEntity<Result>(result, HttpStatus.OK);
        }
        return new ResponseEntity<Result>(result, HttpStatus.OK);
    }

    private EntityModel<GameDto> getResource(Game game) {
        EntityModel<GameDto> resource = EntityModel.of(GameMapper.INSTANCE.toGameDto(game));
        Link selfLink = WebMvcLinkBuilder
                .linkTo(this.getClass()).slash(game.getGuid()).withSelfRel();
        resource.add(selfLink);
        return resource;
    }
}
