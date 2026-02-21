import http from "k6/http";
import { sleep } from "k6";
import { Counter } from "k6/metrics";

const status_0 = new Counter("status_0_timeout"); // timeout/network error (k6에서는 status=0)
const status_5xx = new Counter("status_5xx"); // 시스템 에러
const soldOut = new Counter("sold_out");
const dup = new Counter("duplicate");

export const options = {
  stages: [
    { duration: "20s", target: 20 }, // warmup
    { duration: "30s", target: 50 },
    { duration: "30s", target: 100 },
    { duration: "30s", target: 200 },
    { duration: "30s", target: 300 },
    { duration: "30s", target: 0 },
  ],
};

const BASE_URL = "http://app:8080";

// ===== 비율 고정 파라미터 =====
const DUP_RATIO = 0.30;         // 30%는 중복 유도
const DUP_POOL_SIZE = 20000;    // 중복 userId 풀 크기 (너무 작으면 DUP가 과하게 쏠림)
const UNIQUE_BASE = 1_000_000;  // 유니크 userId 시작값(중복 풀과 겹치지 않게)

// 간단 LCG (결정적 난수) - Math.random()의 흔들림 줄이기
function lcg(seed) {
  // 32-bit LCG
  return (seed * 1664525 + 1013904223) >>> 0;
}

export default function () {
  // 실행마다 분포가 비슷하도록: vu/iter 기반 seed
  const vu = (__VU || 1) >>> 0;
  const it = (__ITER || 0) >>> 0;

  const seed = lcg(lcg(vu) ^ it);
  const r = (seed % 10000) / 10000; // 0.0000 ~ 0.9999

  let userId;

  if (r < DUP_RATIO) {
    // ===== 30%: 중복 유도 =====
    // 같은 pool 안에서 반복되도록 결정적으로 선택
    // (쿠폰이 이미 발급된 userId들이 계속 재요청 -> DUPLICATE 비율이 안정적)
    userId = (seed % DUP_POOL_SIZE) + 1; // 1 ~ DUP_POOL_SIZE
  } else {
    // ===== 70%: 유니크 =====
    // 매 iteration마다 새로운 userId를 만들기
    // (재고가 남아있다면 SUCCESS가 주로 나오고, 소진되면 SOLD_OUT으로 바뀜)
    userId = UNIQUE_BASE + vu * 10_000_000 + it; // 충분히 크게 벌려 충돌 방지
  }

  const payload = JSON.stringify({ userId });

  const params = {
    headers: { "Content-Type": "application/json" },
    timeout: "10s",
  };

  const res = http.post(`${BASE_URL}/coupons/1/issue`, payload, params);

  if (res.status === 0) status_0.add(1);
  else if (res.status >= 500) status_5xx.add(1);
  else if (res.status === 409) {
    const code = JSON.parse(res.body).code;
    if (code === "SOLD_OUT") soldOut.add(1);
    if (code === "DUPLICATE") dup.add(1);
  }

  sleep(0.02);
}
