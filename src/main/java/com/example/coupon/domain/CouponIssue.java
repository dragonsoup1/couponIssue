package com.example.coupon.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_issue",
        uniqueConstraints = @UniqueConstraint(name = "uq_coupon_user", columnNames = {"coupon_id", "user_id"})
)
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    protected CouponIssue() {}

    private CouponIssue(Long couponId, Long userId) {
        this.couponId = couponId;
        this.userId = userId;
    }

    public static CouponIssue of(Long couponId, Long userId) {
        return new CouponIssue(couponId, userId);
    }

    @PrePersist
    void prePersist() {
        if (issuedAt == null) issuedAt = LocalDateTime.now();
    }

    public Long getIssueId() { return issueId; }
    public Long getCouponId() { return couponId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
}
