package com.project.clariti;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ComponentScan("com.project.clariti")
@TestPropertySource(locations = "classpath:application-test.properties")
class SpringBootProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}