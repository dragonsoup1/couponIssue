// src/main/java/com/example/coupon/service/CouponIssueTxService.java
package com.example.coupon.service;

import com.example.coupon.repository.CouponIssueRepository;
import com.example.coupon.repository.CouponStockBucketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponIssueTxService {

    private final CouponIssueRepository issueRepository;
    private final CouponStockBucketRepository bucketRepository;

    public CouponIssueTxService(CouponIssueRepository issueRepository,
                                CouponStockBucketRepository bucketRepository) {
        this.issueRepository = issueRepository;
        this.bucketRepository = bucketRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IssueResult tryIssueOnce(Long couponId, Long userId, int bucketNo) {
        int inserted = issueRepository.insertIgnore(couponId, userId);
        if (inserted == 0) return IssueResult.DUPLICATE;

        int decreased = bucketRepository.decreaseIfAvailable(couponId, bucketNo);
        if (decreased == 1) return IssueResult.SUCCESS;

        throw new BucketUnavailableException();
    }

    public static class BucketUnavailableException extends RuntimeException {}
}
