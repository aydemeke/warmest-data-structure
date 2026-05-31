package com.example.warmest.service;

import com.example.warmest.core.WarmestDataStructureInterface;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(
        name = "warmest.backend",
        havingValue = "redis"
)
public class RedisWarmestDataStructureService implements WarmestDataStructureInterface {

    // ----- Redis key names -------------------------------------------------
    private static final String VALUES_KEY = "warmest:values";
    private static final String NEXT_KEY = "warmest:next";
    private static final String PREV_KEY = "warmest:prev";
    private static final String HEAD_KEY = "warmest:head";
    private static final String TAIL_KEY = "warmest:tail";

    private static final List<String> KEYS = List.of(
            VALUES_KEY, NEXT_KEY, PREV_KEY, HEAD_KEY, TAIL_KEY
    );

    // ----- Lua scripts -----------------------------------------
    private final RedisScript<String> putScript = loadScript("redis/put.lua");
    private final RedisScript<String> getScript = loadScript("redis/get.lua");
    private final RedisScript<String> removeScript = loadScript("redis/remove.lua");

    private final StringRedisTemplate redis;

    public RedisWarmestDataStructureService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Integer put(String key, int value) {
        validateKey(key);
        String previous = redis.execute(putScript, KEYS, key, Integer.toString(value));
        return parse(previous);
    }

    @Override
    public Integer remove(String key) {
        validateKey(key);
        String previous = redis.execute(removeScript, KEYS, key);
        return parse(previous);
    }

    @Override
    public Integer get(String key) {
        validateKey(key);
        String value = redis.execute(getScript, KEYS, key);
        return parse(value);
    }


    @Override
    public String getWarmest() {
        return redis.opsForValue().get(HEAD_KEY);
    }

    // ----- helpers ---------------------------------------------------------

    private static RedisScript<String> loadScript(String path) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        script.setResultType(String.class);
        return script;
    }

    private static Integer parse(String value) {
        return value == null ? null : Integer.valueOf(value);
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
    }
}
