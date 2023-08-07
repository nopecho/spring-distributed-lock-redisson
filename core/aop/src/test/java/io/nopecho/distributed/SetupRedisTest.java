package io.nopecho.distributed;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

import java.io.IOException;

public class SetupRedisTest {

    static RedisServer server;

    @BeforeAll
    static void beforeAll() throws IOException {
        server = new RedisServer(6379);
        server.start();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }
}
