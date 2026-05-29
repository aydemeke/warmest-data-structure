package com.example.warmest.service;

import com.example.warmest.api.WarmestDataStructureInterface;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "warmest.backend",
        havingValue = "redis"
)
public class RedisWarmestDataStructureService implements WarmestDataStructureInterface {
    private static final String VALUES_KEY = "warmest:values";
    private static final String ORDER_KEY = "warmest:order";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisWarmestDataStructureService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Integer put(String key, int value) {
        validateKey(key);

        Object existing = redisTemplate.opsForHash().get(VALUES_KEY, key);

        Integer previous = existing != null
                ? Integer.valueOf(existing.toString())
                : null;

        redisTemplate.opsForHash().put(VALUES_KEY, key, value);
        redisTemplate.opsForList().remove(ORDER_KEY, 0, key);
        redisTemplate.opsForList().leftPush(ORDER_KEY, key);

        return previous;
    }

    @Override
    public Integer remove(String key) {
        validateKey(key);

        Object existing = redisTemplate.opsForHash() .get(VALUES_KEY, key);

        if (existing == null)
            return null;

        redisTemplate.opsForHash() .delete(VALUES_KEY, key);
        redisTemplate.opsForList() .remove(ORDER_KEY, 0, key);

        return Integer.valueOf(existing.toString());
    }

    @Override
    public Integer get(String key) {
        validateKey(key);

        Object value = redisTemplate.opsForHash().get(VALUES_KEY, key);
        if (value == null)
            return null;

        redisTemplate.opsForList().remove(ORDER_KEY, 0, key);
        redisTemplate.opsForList() .leftPush(ORDER_KEY, key);

        return Integer.valueOf(value.toString());
    }

    @Override
    public String getWarmest() {
        Object value = redisTemplate.opsForList()
                .index(ORDER_KEY, 0);

        return value != null ? value.toString(): null;
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
    }
}
