# coupon-troubleshooting (Spring Boot + JPA + MySQL + k6)

로컬(노트북)에서 **리소스 제한 환경**을 만들고, **부하 테스트**로 p95/p99 급증 및 DB 과부하를 재현하기 위한 최소 예제 프로젝트입니다.

## 준비물
- Docker Desktop

## 실행 (앱 + DB)
```bash
docker compose up -d --build
```

확인:
- http://localhost:8080/actuator/health
- API: POST http://localhost:8080/coupons/1/issue
  - body: {"userId": 123}

## 부하 테스트 실행 (k6)
```bash
docker compose --profile loadtest up k6
```

## 리소스(과부하) 확인
다른 터미널에서:
```bash
docker stats
```

MySQL 내부 확인(선택):
```bash
docker ps
docker exec -it <mysql_container> mysql -uroot -proot -e "SHOW GLOBAL STATUS LIKE 'Threads_running';"
docker exec -it <mysql_container> mysql -uroot -proot -e "SHOW GLOBAL STATUS LIKE 'Innodb_row_lock%';"
docker exec -it <mysql_container> mysql -uroot -proot -e "SHOW ENGINE INNODB STATUS\G"
```

## 실험 팁
- `docker-compose.yml`에서 mysql cpus를 0.25로 줄이면 더 빨리 병목이 납니다.
- app의 Hikari max pool size(20)를 10/30/50으로 바꿔가며 커넥션 대기를 관찰할 수 있습니다.

## 초기 로직(문제 상황)
- 남은 수량 SELECT
- 중복 확인 SELECT
- 통과 시 remaining_count -1 UPDATE + 발급 INSERT
- 쿠폰 정책 row에 PESSIMISTIC_WRITE 락을 걸어 lock contention을 쉽게 재현
