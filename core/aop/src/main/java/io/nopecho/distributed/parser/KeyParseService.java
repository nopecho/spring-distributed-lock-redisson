package io.nopecho.distributed.parser;

public interface KeyParseService {
    String parseDynamicKey(String[] paramNames, Object[] args, String key);
}
