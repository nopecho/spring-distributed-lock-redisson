package io.nopecho.distributed;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

public class SetupTestRedis {

    static RedisServer server;

    @BeforeAll
    static void beforeAll() {
        try {
            server = new RedisServer(6666);
            server.start();
        } catch (Exception e) {
            System.out.println("failed test redis server start! :(");
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            server.stop();
        } finally {
            server = null;
        }
    }
}
