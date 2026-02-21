package com.example.coupon.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "coupon_stock_bucket")
@IdClass(CouponStockBucketId.class)
public class CouponStockBucket {

    @Id
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Id
    @Column(name = "bucket_no", nullable = false)
    private Integer bucketNo;

    @Column(name = "remaining_count", nullable = false)
    private Integer remainingCount;

    protected CouponStockBucket() {}
}
