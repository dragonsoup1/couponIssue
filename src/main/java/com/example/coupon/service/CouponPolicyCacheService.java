package com.example.coupon.service;

import com.example.coupon.domain.CouponPolicy;
import com.example.coupon.repository.CouponPolicyRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class CouponPolicyCacheService {

    private static final Duration INIT_LOCK_TTL = Duration.ofSeconds(3);
    private static final int WAIT_TRIES = 20;
    private static final long WAIT_SLEEP_MS = 30;

    private final CouponPolicyRepository policyRepository;
    private final StringRedisTemplate redis;

    public CouponPolicyCacheService(CouponPolicyRepository policyRepository,
                                    StringRedisTemplate redis) {
        this.policyRepository = policyRepository;
        this.redis = redis;
    }

    public void ensurePolicyLoaded(Long couponId) {
        String policyKey = CouponRedisGateService.policyKey(couponId);

        // 이미 있으면 끝
        Boolean has = redis.opsForHash().hasKey(policyKey, "total_count");
        if (Boolean.TRUE.equals(has)) return;

        String lockKey = CouponRedisGateService.initLockKey(couponId);

        // init lock 획득 시도 (딱 1개 인스턴스만 초기화)
        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, "1", INIT_LOCK_TTL);
        if (Boolean.TRUE.equals(locked)) {
            try {
                // 다시 한 번 체크 (경쟁 상황 방어)
                has = redis.opsForHash().hasKey(policyKey, "total_count");
                if (Boolean.TRUE.equals(has)) return;

                CouponPolicy policy = policyRepository.findById(couponId)
                        .orElseThrow(() -> new IllegalStateException("coupon_policy not found: " + couponId));

                redis.opsForHash().putAll(policyKey, Map.of(
                        "total_count", String.valueOf(policy.getTotalCount()),
                        "start_at", policy.getStartAt().toString(),
                        "end_at", policy.getEndAt().toString()
                ));

                // 정책 TTL을 걸고 싶으면 여기서 EXPIRE (옵션)
                // redis.expire(policyKey, Duration.ofHours(48));

                return;

            } finally {
                redis.delete(lockKey); // 락 해제
            }
        }

        // 락 못 잡았으면: 짧게 기다렸다가 policy 생기면 진행
        for (int i = 0; i < WAIT_TRIES; i++) {
            has = redis.opsForHash().hasKey(policyKey, "total_count");
            if (Boolean.TRUE.equals(has)) return;
            sleep(WAIT_SLEEP_MS);
        }

        // 여기까지 왔으면 init이 실패/지연 중. (운영이면 503 처리 추천)
        throw new IllegalStateException("policy cache init timeout: " + couponId);
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
