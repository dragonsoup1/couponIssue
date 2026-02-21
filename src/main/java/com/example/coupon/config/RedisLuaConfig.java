package com.example.coupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisLuaConfig {

    /**
     * 반환값:
     *  -2 : total 미설정
     *  -1 : DUPLICATE
     *   0 : SOLD_OUT
     *  >0 : rank(선착순 번호)
     */
    @Bean
    public DefaultRedisScript<Long> couponReserveLua() {
        String lua = """
            local total = tonumber(redis.call('GET', KEYS[2]))
            if not total then
              return -2
            end

            if redis.call('ZSCORE', KEYS[1], ARGV[1]) then
              return -1
            end

            local issued = redis.call('ZCARD', KEYS[1])
            if issued >= total then
              return 0
            end

            local rank = redis.call('INCR', KEYS[3])
            redis.call('ZADD', KEYS[1], rank, ARGV[1])
            return rank
        """;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(lua);
        script.setResultType(Long.class);
        return script;
    }
}
