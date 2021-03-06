package com.campsite.reservation;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.campsite.reservation.service.ReservationService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void contextLoads() {
        assertThat(ctx).isNotNull();
        AssertionsForClassTypes.assertThat(ctx.getBean(ReservationService.class)).isNotNull();
    }

}
