package io.nopecho.distributed.services;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class SpelParseService implements KeyParseService {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String parseDynamicKey(String[] paramNames, Object[] args, String key) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
