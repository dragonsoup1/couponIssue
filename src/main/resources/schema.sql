-- 1) coupon_policy: 발급 현황 확인용 메타/집계
CREATE TABLE IF NOT EXISTS coupon_policy (
                                             coupon_id   BIGINT PRIMARY KEY,
                                             name        VARCHAR(100) NOT NULL,
    total_count INT NOT NULL,
    start_at    DATETIME NOT NULL,
    end_at      DATETIME NOT NULL
    ) ENGINE=InnoDB;

-- 2) coupon_issue: 실제 발급 기록 (중복 발급 방지)
CREATE TABLE IF NOT EXISTS coupon_issue (
                                            issue_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            coupon_id BIGINT NOT NULL,
                                            user_id   BIGINT NOT NULL,
                                            issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            CONSTRAINT uq_coupon_user UNIQUE (coupon_id, user_id),
    INDEX idx_coupon_issued_at (coupon_id, issued_at)
    ) ENGINE=InnoDB;

-- 3) coupon_stock_bucket: 재고 분산(버킷)
CREATE TABLE IF NOT EXISTS coupon_stock_bucket (
                                                   coupon_id       BIGINT NOT NULL,
                                                   bucket_no       INT NOT NULL,
                                                   remaining_count INT NOT NULL,
                                                   PRIMARY KEY (coupon_id, bucket_no),
    CHECK (remaining_count >= 0)
    ) ENGINE=InnoDB;
