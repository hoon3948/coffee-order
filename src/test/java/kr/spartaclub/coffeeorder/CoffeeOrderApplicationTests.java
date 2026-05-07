package kr.spartaclub.coffeeorder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.spartaclub.coffeeorder.config.TestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class CoffeeOrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
