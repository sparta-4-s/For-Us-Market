# 📦 E-Commerce Platform 📦

## **💻 프로젝트 소개**

본 프로젝트는 **대형 쇼핑몰 실시간 플랫폼**을 구현한 E-Commerce 시스템입니다.

사용자는 상품을 등록·검색·주문할 수 있으며, 주문 시 발생할 수 있는 **재고 동시성 문제**를 Redis 분산락과 JPA 락을 통해 안정적으로 처리합니다.

또한, Redis Sorted Set을 활용한 **실시간 인기 검색어 집계**, AOP 기반 **주문 로그 기록**을 통해 검색 트렌드 분석과 서비스 모니터링을 지원합니다.

## **🔗 ERD**
<img width="1484" height="825" alt="Image" src="https://github.com/user-attachments/assets/a70c2784-1d1e-498e-89c9-0590a49287be" />

## **📋** 주요 기능

### 1. 사용자 관리

- 로그인 / JWT 인증
- 유저 프로필 조회

### 2. 상품

- 상품 등록 / 조회 API
- 카테고리 구체화
- 대량 상품 데이터 처리

### 3. 주문

- 주문 생성 / 취소
- 주문 상태 관리
- 트랜잭션 기반 재고 검증 및 차감 (락 적용)

### 4. 주문 로그

- 주문 로그 AOP 수집
- 사용자 주문 로그 수집 및 조회

### 5. 검색 및 집계

- 상품 검색 API
- 인기 검색어 집계

---

## **🎯** 성능 개선
<details>
  <summary>📌 Access & Refresh 인증 관리</summary>

- Spring Security + JWT 기반 로그인/인증 시스템  
- Redis 캐시 서버를 활용한 Refresh Token 저장 및 블랙리스트 관리  
- Refresh Token 로테이션 전략 적용으로 토큰 재사용 방지  
- HttpOnly 쿠키 적용으로 XSS 기반 토큰 탈취 위험 최소화  

<img width="820" height="473" alt="Image" src="https://github.com/user-attachments/assets/00ed8517-eec7-4236-a0de-f90c29e07e3b" />

</details>    

<details>
  <summary>📌 단계별 쿼리 개선</summary>

### **실험 환경**
- 500만 건 상품 데이터, 페이지 크기(size) 60  
- OFFSET: 0 (처음), 90,000 (중간), 208,200 (끝)  

### **비교 대상**
- 단순 Offset 기반 페이징  
- **복합 인덱스** (`subcategory, updated_at, id`)  
- **커버링 인덱스** (`subcategory, updated_at, id` + SELECT 컬럼 포함)  
- **No-Offset 방식**  

### **성능 결과: Offset vs No-Offset**

**Offset 방식**

| 페이지 위치 | OFFSET   | 인덱스 없음 | 복합 인덱스 | 커버링 인덱스 |
|-------------|----------|-------------|-------------|---------------|
| **Page 0**  | 0        | 7.05s       | 83ms        | 81ms          |
| **Page 1500** | 90,000 | 7.65s       | 17.96s      | 118ms         |
| **Page 3470** | 208,200 | 7.47s      | 42.69s      | 155ms         |

**No-Offset 방식**

| 페이지 위치 | OFFSET   | 조회 범위 (rows) | 인덱스 없음 | 복합 인덱스 *(Extra: Using index condition)* |
|-------------|----------|------------------|-------------|---------------------------------------------|
| **Page 0**  | 0        | 처음부터 60개     | 4.35s       | 9ms                                         |
| **Page 1500** | 90,000 | 90,000 + 60개    | 4.27s       | 12ms                                        |
| **Page 3470** | 208,200 | 208,200 + 60개   | 4.25s       | 10ms                                        |

</details>
    
<details>
  <summary>📌 동시성 제어</summary>

### 개선 전
- 주문 시 동시에 재고 차감이 일어나면 **오버셀링**, DB 락 시 **커넥션 풀 고갈** 문제 발생  

### 고려한 대안
- **비관적 락**: 정합성 ↑ / 커넥션 풀 대기, 성능 저하  
- **낙관적 락**: 충돌 적으면 빠름 / 경쟁 많을 때 롤백 폭증  
- **Lettuce 분산락**: TTL로 데드락 방지 / 스핀락 부하, 트랜잭션 언락 타이밍 이슈  
- **Redisson 분산락**: pub/sub 기반, 소유자 검증·leaseTime 내장, 운영 편의성 ↑  

### 고민한 점
- 트랜잭션 종료와 락 해제 시점 불일치 문제 → `afterCommit()`으로 해결  
- 스핀락의 Redis 부하 vs Redisson pub/sub의 안정성  
- 운영 편의성과 유지보수성 고려  

### 성능 비교 (테스트: 동시 요청 50건, 재고 100개)

| 방식 | 처리 시간 | 성공률 | 비고 |
|------|----------|--------|------|
| 비관적 락 | ~3.2s | 100% | 커넥션 풀 10 초과 시 **Timeout** 발생 |
| 낙관적 락 | ~2.8s | 60% | 충돌 발생 시 대부분 롤백 |
| Lettuce (스핀락) | ~2.5s | 100% | Redis CPU 부하 ↑ |
| **Redisson (최종)** | **~1.9s** | **100%** | 안정적 처리, 부하 ↓ |

### 최종 선택
- **Redisson 분산락**  
  - 정합성 보장, 성능 안정성 확보  
  - Lettuce 직접 구현 대비 단순하고 운영이 용이  
  - DB 락 대비 부하 감소  

### 동시성 테스트 속도 비교
<img width="586" height="590" alt="Image" src="https://github.com/user-attachments/assets/1d935ccb-941c-43a2-8953-8eba027bbfa7" />

</details>


<details>
  <summary>📌 로그 기록 조회 성능 개선 (캐시 적용 전/후)</summary>

### 개선 전
- 동일 유저 로그 반복 조회할 때 매번 DB에서 SELECT 발생  
- 조회량이 많아지면 DB에 과부하  
- 초기 로그 조회 속도: 337ms  

### 개선 과정
- 캐시 적용 (Redis)  
- 337ms → 19ms → 14ms  

### 개선 내용
- Spring Cache + Redis 적용 (@Cacheable)  
- 반복 조회 시 DB 쿼리 발생 없이 Redis에서 즉시 응답  

### ⏳ 캐시 적용 전 337ms
<img width="854" height="726" alt="Image" src="https://github.com/user-attachments/assets/86e54570-ed49-405f-ad22-2cb834365909" />

### ⏳ 캐시 적용 후 19ms
<img width="826" height="728" alt="Image" src="https://github.com/user-attachments/assets/cb588855-443b-428d-b2a1-a16a08a9d9e0" />

### 성능 비교

| 구분 | 캐시 적용 전 | 캐시 적용 후 |
|------|--------------|--------------|
| 응답 시간 | 337ms | 19ms |
| DB 조회 | 동일 요청 시 쿼리 반복 발생 | 첫 요청만 쿼리가 발생하고, 이후 Redis 조회 |
| 차이점 | 부하 상황에서 병목 발생 | 요청 많을수록 캐시 히트율 ↑ |

</details>


---

## **📝 API 명세서**

[▶️ API 명세서](https://www.notion.so/S-A-Starting-Assignments-2692dc3ef514814cb474f5bc0446e863?pvs=21)



## **👥 팀원**
김기수 - [GitHub](https://github.com/Lunarltn)

원세영 - [GitHub](https://github.com/seyoung5744)

이은진 - [GitHub](https://github.com/eunjin0468)

이호용 – [GitHub](https://github.com/nyong0313)

정은서 - [GitHub](https://github.com/eunseo04)
