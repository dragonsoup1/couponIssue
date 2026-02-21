package com.example.coupon.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponRedisGateService {

    public enum GateResult { SUCCESS, DUPLICATE, SOLD_OUT, NOT_READY }

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> reserveScript;

    public CouponRedisGateService(StringRedisTemplate redis) {
        this.redis = redis;

        // KEYS[1] = policyKey (HASH)   coupon:{couponId}:policy
        // KEYS[2] = reqKey    (ZSET)   coupon:{couponId}:req
        // ARGV[1] = userId
        // return:
        // 1=SUCCESS, 2=DUPLICATE, 3=SOLD_OUT, 4=NOT_READY
        String lua = """
            local policyKey = KEYS[1]
            local reqKey    = KEYS[2]
            local userId    = ARGV[1]

            local total = redis.call('HGET', policyKey, 'total_count')
            if (not total) then
              return 4
            end
            total = tonumber(total)

            local existed = redis.call('ZSCORE', reqKey, userId)
            if (existed) then
              return 2
            end

            local current = redis.call('ZCARD', reqKey)
            if (current >= total) then
              return 3
            end

            local t = redis.call('TIME')
            local score = (tonumber(t[1]) * 1000) + math.floor(tonumber(t[2]) / 1000)

            redis.call('ZADD', reqKey, score, userId)
            return 1
        """;

        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setScriptText(lua);
        this.reserveScript.setResultType(Long.class);
    }

    public GateResult reserve(Long couponId, Long userId) {
        String policyKey = policyKey(couponId);
        String reqKey = reqKey(couponId);

        Long r = redis.execute(reserveScript, List.of(policyKey, reqKey), String.valueOf(userId));
        long code;
        code = r;

        return switch ((int) code) {
            case 1 -> GateResult.SUCCESS;
            case 2 -> GateResult.DUPLICATE;
            case 3 -> GateResult.SOLD_OUT;
            default -> GateResult.NOT_READY;
        };
    }

    public void cancel(Long couponId, Long userId) {
        redis.opsForZSet().remove(reqKey(couponId), String.valueOf(userId));
    }

    public static String policyKey(Long couponId) {
        return "coupon:" + couponId + ":policy";
    }

    public static String reqKey(Long couponId) {
        return "coupon:" + couponId + ":req";
    }

    public static String initLockKey(Long couponId) {
        return "coupon:" + couponId + ":init_lock";
    }
}
