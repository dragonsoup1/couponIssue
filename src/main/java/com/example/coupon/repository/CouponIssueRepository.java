// src/main/java/com/example/coupon/repository/CouponIssueRepository.java
package com.example.coupon.repository;

import com.example.coupon.domain.CouponIssue;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT IGNORE INTO coupon_issue (coupon_id, user_id, issued_at)
        VALUES (:couponId, :userId, NOW())
        """, nativeQuery = true)
    int insertIgnore(@Param("couponId") Long couponId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    int deleteByCouponIdAndUserId(Long couponId, Long userId);
}
