package com.example.coupon.service;

import com.example.coupon.repository.CouponIssueRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CouponIssueService {

    private final CouponPolicyCacheService policyCacheService;
    private final CouponRedisGateService redisGateService;
    private final CouponIssueRepository issueRepository;

    public CouponIssueService(CouponPolicyCacheService policyCacheService,
                              CouponRedisGateService redisGateService,
                              CouponIssueRepository issueRepository) {
        this.policyCacheService = policyCacheService;
        this.redisGateService = redisGateService;
        this.issueRepository = issueRepository;
    }

    public IssueResult issue(Long couponId, Long userId) {
        // 1) Redis에 정책 없으면 MySQL에서 로딩해서 Redis에 적재(1회만)
        policyCacheService.ensurePolicyLoaded(couponId);

        // 2) Redis에서 "선착순 + 중복 + 수량" 조건을 원자적으로 통과시키며 예약(= Redis에 발급데이터 삽입)
        CouponRedisGateService.GateResult gate = redisGateService.reserve(couponId, userId);

        if (gate == CouponRedisGateService.GateResult.DUPLICATE) return IssueResult.DUPLICATE;
        if (gate == CouponRedisGateService.GateResult.SOLD_OUT) return IssueResult.SOLD_OUT;
        if (gate == CouponRedisGateService.GateResult.NOT_READY) {
            // 정책이 없거나 로딩 실패 케이스 (정상이라면 거의 안 옴)
            return IssueResult.SOLD_OUT;
        }

        // 3) MySQL에는 insert만 수행 (unique constraint + INSERT IGNORE로 최종 중복 방어)
        try {
            int inserted = issueRepository.insertIgnore(couponId, userId);
            if (inserted == 0) {
                // Redis에선 통과했는데 DB에서 중복이면, Redis 예약을 되돌림
                redisGateService.cancel(couponId, userId);
                return IssueResult.DUPLICATE;
            }
            return IssueResult.SUCCESS;

        } catch (DataAccessException e) {
            // DB insert 실패면 Redis 예약을 되돌려야 다음 사람이 기회를 가짐
            redisGateService.cancel(couponId, userId);
            throw e;
        }
    }
}
