package io.nopecho.distributed.services;

@Deprecated
public interface KeyParseService {
    String parseDynamicKey(String[] paramNames, Object[] args, String key);
}
