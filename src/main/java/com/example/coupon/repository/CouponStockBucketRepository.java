package com.example.coupon.repository;

import com.example.coupon.domain.CouponStockBucket;
import com.example.coupon.domain.CouponStockBucketId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CouponStockBucketRepository extends JpaRepository<CouponStockBucket, CouponStockBucketId> {

    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE coupon_stock_bucket
           SET remaining_count = remaining_count - 1
         WHERE coupon_id = :couponId
           AND bucket_no = :bucketNo
           AND remaining_count > 0
        """, nativeQuery = true)
    int decreaseIfAvailable(@Param("couponId") Long couponId,
                            @Param("bucketNo") int bucketNo);
}
