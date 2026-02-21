-- ====== 설정값 ======
SET @coupon_id = 1;
SET @name = 'FIRST_COME_COUPON';
SET @total = 100000;
SET @bucket_count = 64;

-- ====== coupon_policy upsert ======
INSERT INTO coupon_policy (coupon_id, name, total_count, start_at, end_at)
VALUES (@coupon_id, @name, @total, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY)
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         total_count = VALUES(total_count),
                         start_at = VALUES(start_at),
                         end_at = VALUES(end_at);

-- ====== bucket seed (재설정 방식: DELETE 후 INSERT) ======
SET @base = FLOOR(@total / @bucket_count);
SET @rem  = MOD(@total, @bucket_count);

DELETE FROM coupon_stock_bucket WHERE coupon_id = @coupon_id;

INSERT INTO coupon_stock_bucket (coupon_id, bucket_no, remaining_count) VALUES
                                                                            (@coupon_id, 0 , @base + IF(0  < @rem, 1, 0)),
                                                                            (@coupon_id, 1 , @base + IF(1  < @rem, 1, 0)),
                                                                            (@coupon_id, 2 , @base + IF(2  < @rem, 1, 0)),
                                                                            (@coupon_id, 3 , @base + IF(3  < @rem, 1, 0)),
                                                                            (@coupon_id, 4 , @base + IF(4  < @rem, 1, 0)),
                                                                            (@coupon_id, 5 , @base + IF(5  < @rem, 1, 0)),
                                                                            (@coupon_id, 6 , @base + IF(6  < @rem, 1, 0)),
                                                                            (@coupon_id, 7 , @base + IF(7  < @rem, 1, 0)),
                                                                            (@coupon_id, 8 , @base + IF(8  < @rem, 1, 0)),
                                                                            (@coupon_id, 9 , @base + IF(9  < @rem, 1, 0)),
                                                                            (@coupon_id, 10, @base + IF(10 < @rem, 1, 0)),
                                                                            (@coupon_id, 11, @base + IF(11 < @rem, 1, 0)),
                                                                            (@coupon_id, 12, @base + IF(12 < @rem, 1, 0)),
                                                                            (@coupon_id, 13, @base + IF(13 < @rem, 1, 0)),
                                                                            (@coupon_id, 14, @base + IF(14 < @rem, 1, 0)),
                                                                            (@coupon_id, 15, @base + IF(15 < @rem, 1, 0)),
                                                                            (@coupon_id, 16, @base + IF(16 < @rem, 1, 0)),
                                                                            (@coupon_id, 17, @base + IF(17 < @rem, 1, 0)),
                                                                            (@coupon_id, 18, @base + IF(18 < @rem, 1, 0)),
                                                                            (@coupon_id, 19, @base + IF(19 < @rem, 1, 0)),
                                                                            (@coupon_id, 20, @base + IF(20 < @rem, 1, 0)),
                                                                            (@coupon_id, 21, @base + IF(21 < @rem, 1, 0)),
                                                                            (@coupon_id, 22, @base + IF(22 < @rem, 1, 0)),
                                                                            (@coupon_id, 23, @base + IF(23 < @rem, 1, 0)),
                                                                            (@coupon_id, 24, @base + IF(24 < @rem, 1, 0)),
                                                                            (@coupon_id, 25, @base + IF(25 < @rem, 1, 0)),
                                                                            (@coupon_id, 26, @base + IF(26 < @rem, 1, 0)),
                                                                            (@coupon_id, 27, @base + IF(27 < @rem, 1, 0)),
                                                                            (@coupon_id, 28, @base + IF(28 < @rem, 1, 0)),
                                                                            (@coupon_id, 29, @base + IF(29 < @rem, 1, 0)),
                                                                            (@coupon_id, 30, @base + IF(30 < @rem, 1, 0)),
                                                                            (@coupon_id, 31, @base + IF(31 < @rem, 1, 0)),
                                                                            (@coupon_id, 32, @base + IF(32 < @rem, 1, 0)),
                                                                            (@coupon_id, 33, @base + IF(33 < @rem, 1, 0)),
                                                                            (@coupon_id, 34, @base + IF(34 < @rem, 1, 0)),
                                                                            (@coupon_id, 35, @base + IF(35 < @rem, 1, 0)),
                                                                            (@coupon_id, 36, @base + IF(36 < @rem, 1, 0)),
                                                                            (@coupon_id, 37, @base + IF(37 < @rem, 1, 0)),
                                                                            (@coupon_id, 38, @base + IF(38 < @rem, 1, 0)),
                                                                            (@coupon_id, 39, @base + IF(39 < @rem, 1, 0)),
                                                                            (@coupon_id, 40, @base + IF(40 < @rem, 1, 0)),
                                                                            (@coupon_id, 41, @base + IF(41 < @rem, 1, 0)),
                                                                            (@coupon_id, 42, @base + IF(42 < @rem, 1, 0)),
                                                                            (@coupon_id, 43, @base + IF(43 < @rem, 1, 0)),
                                                                            (@coupon_id, 44, @base + IF(44 < @rem, 1, 0)),
                                                                            (@coupon_id, 45, @base + IF(45 < @rem, 1, 0)),
                                                                            (@coupon_id, 46, @base + IF(46 < @rem, 1, 0)),
                                                                            (@coupon_id, 47, @base + IF(47 < @rem, 1, 0)),
                                                                            (@coupon_id, 48, @base + IF(48 < @rem, 1, 0)),
                                                                            (@coupon_id, 49, @base + IF(49 < @rem, 1, 0)),
                                                                            (@coupon_id, 50, @base + IF(50 < @rem, 1, 0)),
                                                                            (@coupon_id, 51, @base + IF(51 < @rem, 1, 0)),
                                                                            (@coupon_id, 52, @base + IF(52 < @rem, 1, 0)),
                                                                            (@coupon_id, 53, @base + IF(53 < @rem, 1, 0)),
                                                                            (@coupon_id, 54, @base + IF(54 < @rem, 1, 0)),
                                                                            (@coupon_id, 55, @base + IF(55 < @rem, 1, 0)),
                                                                            (@coupon_id, 56, @base + IF(56 < @rem, 1, 0)),
                                                                            (@coupon_id, 57, @base + IF(57 < @rem, 1, 0)),
                                                                            (@coupon_id, 58, @base + IF(58 < @rem, 1, 0)),
                                                                            (@coupon_id, 59, @base + IF(59 < @rem, 1, 0)),
                                                                            (@coupon_id, 60, @base + IF(60 < @rem, 1, 0)),
                                                                            (@coupon_id, 61, @base + IF(61 < @rem, 1, 0)),
                                                                            (@coupon_id, 62, @base + IF(62 < @rem, 1, 0)),
                                                                            (@coupon_id, 63, @base + IF(63 < @rem, 1, 0));
