package com.devops.qas.tests.recommendation;

import com.devops.qas.tests.TestsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestsApplicationTest {

    @Test
    void contextLoads() {
        // Test context loading
    }

    @Test
    void main() {
        TestsApplication.main(new String[]{"--server.port=0"});
    }
}
