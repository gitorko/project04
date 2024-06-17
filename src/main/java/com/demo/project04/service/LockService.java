package com.demo.project04.service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Interact with Ignite as key-value store (non-persistent store)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    final Ignite ignite;
    IgniteCache<String, String> cache;

    @Value("${ignite.nodeName:node0}")
    private String nodeName;

    @PostConstruct
    public void postInit() {
        cache = ignite.cache("lock-cache");
    }

    public String runJob(Integer seconds) {
        log.info("Acquiring Lock by {}", nodeName);
        Lock myLock = cache.lock("lock-01");
        try {
            myLock.lock();
            return executeTask(seconds);
        } finally {
            myLock.unlock();
        }
    }

    @SneakyThrows
    private String executeTask(Integer seconds) {
        log.info("Starting job by {}", nodeName);
        log.info("Sleeping for {} secs", seconds);
        TimeUnit.SECONDS.sleep(seconds);
        log.info("Finished job by {}", nodeName);
        return "Job completed by " + nodeName + " @ " + Instant.now();
    }

}
