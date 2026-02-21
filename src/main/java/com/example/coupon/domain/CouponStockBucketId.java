package com.example.coupon.domain;

import java.io.Serializable;
import java.util.Objects;

public class CouponStockBucketId implements Serializable {
    private Long couponId;
    private Integer bucketNo;

    public CouponStockBucketId() {}

    public CouponStockBucketId(Long couponId, Integer bucketNo) {
        this.couponId = couponId;
        this.bucketNo = bucketNo;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CouponStockBucketId that)) return false;
        return Objects.equals(couponId, that.couponId) && Objects.equals(bucketNo, that.bucketNo);
    }

    @Override public int hashCode() {
        return Objects.hash(couponId, bucketNo);
    }
}
