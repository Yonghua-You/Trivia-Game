package com.game.trivia;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.game.trivia.service.GameService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TriviaApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void contextLoads() {
        assertThat(ctx).isNotNull();
        AssertionsForClassTypes.assertThat(ctx.getBean(GameService.class)).isNotNull();
    }

}
