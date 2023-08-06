package io.nopecho.distributed.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpelParseService implements KeyParseService {

    private final SpelExpressionParser parser;

    @Override
    public String parseDynamicKey(String[] paramNames, Object[] args, String key) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
