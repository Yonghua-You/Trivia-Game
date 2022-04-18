package com.game.trivia.domain.mapper;

import com.game.trivia.domain.Game;
import com.game.trivia.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);
    Game toGame(GameDto gameDto);
    GameDto toGameDto(Game game);
}
