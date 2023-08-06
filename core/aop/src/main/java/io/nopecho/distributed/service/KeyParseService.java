package io.nopecho.distributed.service;

public interface KeyParseService {
    String parseDynamicKey(String[] paramNames, Object[] args, String key);
}
