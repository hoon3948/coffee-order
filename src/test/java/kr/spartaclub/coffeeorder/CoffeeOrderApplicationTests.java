package kr.spartaclub.coffeeorder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import kr.spartaclub.coffeeorder.config.TestConfig;

@SpringBootTest
@Import(TestConfig.class)
class CoffeeOrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
