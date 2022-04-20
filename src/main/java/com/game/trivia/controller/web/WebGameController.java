package com.game.trivia.controller.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.game.trivia.domain.Answer;
import com.game.trivia.domain.Game;
import com.game.trivia.domain.Question;
import com.game.trivia.service.GameService;
import com.game.trivia.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class WebGameController {

	@Autowired
	GameService gameService;

	@Autowired
	QuestionService questionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home");
		return mav;
	}



	@RequestMapping(value = "/game/{guid}", method = RequestMethod.GET)
	public ModelAndView getGame(@PathVariable UUID guid) {
		Game game = gameService.searchGameByGuid(guid);
		ModelAndView mav = new ModelAndView();
		mav.addObject("game", game);
		mav.setViewName("gameView");

		return mav;
	}

	@RequestMapping(value = "/game/{guid}/question/{question_id}/answers", method = RequestMethod.GET)
	public ModelAndView getAnswers(
			@PathVariable(name = "guid") String guid,
			@PathVariable(name = "question_id") Long question_id
	) {
		Game game = gameService.searchGameByGuid(UUID.fromString(guid));
		List<Question> questions = game.getQuestions();
		Question q = questions.stream().filter(question->question.getId().equals(question_id)).findFirst().get();
		ModelAndView mav = new ModelAndView();
		mav.addObject("answers", q.getAnswers());
		mav.setViewName("answerStatistics");
		return mav;
	}

	@RequestMapping(value = "/game/{guid}/play", method = RequestMethod.GET)
	public ModelAndView playGame(@PathVariable UUID guid, HttpSession session) {
		if(!session.isNew() && session.getAttribute("guid").equals(guid.toString()))
		{
			ModelAndView mav = new ModelAndView();
			mav.addObject("tip", "You have played the game!");
			mav.setViewName("home");
			return mav;
		}else{
			session.setAttribute("guid", guid);
		}

		System.out.println(session.getId());
		Game game = gameService.searchGameByGuid(guid);

		ModelAndView mav = new ModelAndView();
		mav.addObject("game", game);
		mav.setViewName("playGame");

		return mav;
	}
}
