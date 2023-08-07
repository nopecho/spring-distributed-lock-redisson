package io.nopecho.distributed.services;

public interface KeyParseService {
    String parseDynamicKey(String[] paramNames, Object[] args, String key);
}
