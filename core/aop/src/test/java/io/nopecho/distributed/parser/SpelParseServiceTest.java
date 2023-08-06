package io.nopecho.distributed.parser;

import io.nopecho.distributed.service.SpelParseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.assertj.core.api.Assertions.*;

class SpelParseServiceTest {

    SpelExpressionParser parser = new SpelExpressionParser();
    SpelParseService sut = new SpelParseService(parser);

    @DisplayName("SpEL 문법으로 Long 값이 String 파싱된다.")
    @Test
    void parseTest() {
        String[] params = {"target"};
        Object[] args = {new Fixture(1L, "lock")};
        String SpEL = "#target.id";

        String actual = sut.parseDynamicKey(params, args, SpEL);

        assertThat(actual).isEqualTo("1");
    }

    @DisplayName("SpEL 문법으로 메서드도 함께 파싱된다.")
    @Test
    void parseTest2() {
        String[] params = {"target"};
        Object[] args = {new Fixture(99L, "concat")};
        String SpEL = "#target.idConcatName()";

        String actual = sut.parseDynamicKey(params, args, SpEL);

        assertThat(actual).isEqualTo("99concat");
    }

    record Fixture(Long id, String name) {
        public String idConcatName() {
            return id.toString().concat(name);
        }
    }
}