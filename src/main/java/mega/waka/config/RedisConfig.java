package mega.waka.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mega.waka.entity.redis.SevenDaysResultHistory;
import mega.waka.entity.redis.ThirtyDaysResultHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Getter
@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {
    @Value(value = "${spring.cache.redis.host}")
    private String host;
    @Value(value = "${spring.cache.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() { // 내장 혹은 외부의 Redis를 연결
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, SevenDaysResultHistory> redisTemplate() { // RedisTemplate을 Bean으로 등록
        RedisTemplate<String, SevenDaysResultHistory> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(SevenDaysResultHistory.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(SevenDaysResultHistory.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ThirtyDaysResultHistory> redisTemplateThirtyDays() { // RedisTemplate을 Bean으로 등록
        RedisTemplate<String, ThirtyDaysResultHistory> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ThirtyDaysResultHistory.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ThirtyDaysResultHistory.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
