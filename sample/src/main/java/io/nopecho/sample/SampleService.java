package io.nopecho.sample;

import io.nopecho.distributed.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

    @DistributedLock(key = "#value")
    @Transactional
    public String doSomething(String value) {
        try {
            Thread.sleep(5000);
            return value;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
