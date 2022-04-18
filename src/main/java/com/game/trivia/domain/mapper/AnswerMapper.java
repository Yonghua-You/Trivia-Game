package com.game.trivia.domain.mapper;
import com.game.trivia.domain.Answer;
import com.game.trivia.dto.AnswerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);
    Answer toAnswer(AnswerDto answerDto);
    AnswerDto toAnswerDto(Answer answer);
}
