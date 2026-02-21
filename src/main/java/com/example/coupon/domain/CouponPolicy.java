package com.example.coupon.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_policy")
public class CouponPolicy {

    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    protected CouponPolicy() {}

    public Long getCouponId() { return couponId; }
    public String getName() { return name; }
    public Integer getTotalCount() { return totalCount; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
}
