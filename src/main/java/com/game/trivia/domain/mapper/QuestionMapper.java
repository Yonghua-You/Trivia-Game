package com.game.trivia.domain.mapper;
import com.game.trivia.domain.Question;
import com.game.trivia.dto.QuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);
    Question toQuestion(QuestionDto questionDto);
    QuestionDto toQuestionDto(Question question);
}
