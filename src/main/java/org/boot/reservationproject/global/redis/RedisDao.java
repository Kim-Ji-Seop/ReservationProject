package org.boot.reservationproject.global.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDao {
  private final RedisTemplate<String, Object> redisTemplate;

  public void setValues(String key, String data) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    values.set(key, data);
  }

  public void setValues(String userEmail, String refreshToken, Long time) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    values.set(userEmail, refreshToken, time, TimeUnit.MILLISECONDS);
  }

  public String getValues(String key) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    return (String) values.get(key);
  }

  public void deleteValues(String key) {
    redisTemplate.delete(key);
  }
}
