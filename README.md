# sixpark - 공연 얘매 사이트

## 프로젝트 소개

**sixpark**는 공연 얘매 사이트로서 REST API 기반 프로젝트입니다.

동시성을 핵심 로직으로 하여 캐싱도 가미한 프로그램으로 진행되었습니다.

### 프로젝트 목표

-콘서트 예매 내역 확인 및 인기 공연 검색 성능 강화
-좌석 동시성 제어
-인기 공연 검색(캐싱)

### 개발 기간

- **2025년 12월 31일(수) ~ 2026년 1월 9일(금)** (총 10일)

## 팀 구성

### 팀명: **용준2와 아이들**

| 이름 | 담당 내용 |
| --- | --- |
| 정용준(팀장) | 공연정보/시간, 외부 API 연동, RANKING(주간/일간 TOP10) |
| 이용준 | 유저/인증인가, AOP|
| 성종민 | 게시글 crud, readme |
| 최승희 | 댓글 crud, ppt |
| 권민서 | 예매내역, 동시성 |

## 🛠 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.8
- **Database**: MySQL 8.4, Redis 
- **ORM**: JPA/Hibernate
- **Security**: Spring Security, JWT
- **Build Tool**: Gradle
- **IDE**: IntelliJ IDEA 2025.3.2

## 구현 기능

<details>
  <summary><strong>2. 사용자 관리 (User Management)</strong></summary>
  
  - 회원가입 및 로그인,로그아웃 기능 (JWT 기반 인증)
  - 사용자 정보 조회, 수정 기능
  - 비밀번호 확인 및 변경 기능
  - 전체 회원 조회 기능
  - 관리자 권한 변경 기능
</details>
<details>
  <summary><strong>2. 공연 정보 (ShowInfo Management)</strong></summary>

  - 외부 API에서 공연 데이터 DB 삽입(공연생성)
  - 공연 스케쥴 생성 기능
  - 공연 장르별 전체 조회 기능 (페이징)
  - 공연 장르별 일간/주간/탑10 조회 기능( 조회수기반 - 캐싱)
  - 공연 상세 조회 기능
  - 공연 수정/식제 기능
  - 공연 검색 기능
</details>
<details>
  <summary><strong>3. 게시글 관리 (Post Management)</strong></summary>

  - 게시글 생성/수정/삭제 기능
  - 게시글 전체 조회 기능
  - 게시글 상세 조회 기능
</details>
<details>
  <summary><strong>4. 댓글 관리 (Comment Management)</strong></summary>

  - 댓글 생성/수정/삭제 기능
  - 댓글 조회 기능 (검색/페이징)
  - 부모 댓글 조회 기능
  - 자식 댓글 조회 기능
</details>
<details>
  <summary><strong>5. 예매내역 관리 (Reservation Management)</strong></summary>

  - 예매 생성/삭제 기능
  - 예매 전체/상세 조회 기능
</details>
<details>
  <summary><strong>6. 좌석 관리 (Seat Management)</strong></summary>

  - 좌석 생성 기능
  - 좌석 확인 기능 (동시성 적용) 
</details>

## 데이터베이스 ERD
<img width="1482" height="722" alt="image2" src="https://github.com/user-attachments/assets/e1d520b5-b837-4ab2-8a9b-50f6c2da2b37" />

## API 명세

<details>
  <summary> 유저/인증인가 </summary>
  
  - `POST /api/auth/signup` - 회원가입
  - `POST /api/auth/login` - 로그인
  - `POST /api/auth/logout` - 로그아웃
  - `PUT /api/users` - 회원정보 수정
  - `DELETE /api/users` - 회원 탈퇴
  - `GET /api/users` - 내 정보 조회
  - `PUT /api/users/password` - 비밀번호 변경
  - `POST /api/users/verify-password` - 비밀번호 확인
  - `GET /api/admin/users` - 전체 회원 조회
  - `PATCH /api/admin/{userId}` 관리자 권한 변경
</details>
<details>
  <summary> 공연정보/시간및장소/스케줄/좌석 </summary>
  
  - `POST /api/admin/showInfoes` - 공연 생성 (KOPIS api 요청)
  - `POST /api/admin/show-schedule` - 공연스게줄 생성
  - `POST /api/admin/seat` - 좌석 생성
  - `GET /api/genre/{genreId}/showInfoes` - 장르별 공연 전체 조회 - 페이징
  - `GET /api/showInfoes/{showInfoId}` - 공연 상세 조회
  - `PUT /api/admin/showInfoes/{showInfoId}` - 공연 수정
  - `DELETE /api/admin/showInfoes/{showInfoId}` - 공연삭제
  - `GET /api/genre/{genreId}/showInfoes/ranking/daily` - 장르별 공연 일간 TOP10 조회 (조회수기반 - 캐싱)
  - `GET /api/genre/{genreId}/showInfoes/ranking/weekly` - 장르별 공연 주간 TOP10 조회 (조회수기반 - 캐싱)
  - `GET /api/showInfoes/v1/search` - 공연 검색(v1)
  - `GET /api/showInfoes/v2/search` - 공연 검색(v2 - 성능개선)
  - `GET /api/showInfoes/v3/search` - 공연 검색(v3 - 성능개선)
  - `GET /api/test/search-showInfo` - 공연 검색 성능 테스트
</details>
<details>
  <summary> 게시글 </summary>

  - `POST /api/posts` - 게시글 생성
  - `GET /api/posts` - 게시글 전체 조회
  - `GET /api/posts/{postId}` - 게시글 상세 조회
  - `PUT /api/posts/{postId}` - 게시글 수정
  - `DELETE /api/posts/{pistId}` - 게시글 삭제
</details>
<details>
  <summary> 댓글 </summary>

  - `POST /api/comments` - 댓글 생성
  - `PUT /api/comments/{commentId}` - 댓글 수정
  - `DELETE /api/comments/{commentId}` - 댓글 삭제
  - `GET /api/comments/search` - 댓글 조회(검색/페이징)
  - `GET /api/comments` -부모 댓글 조회
  - `GET /api/comments/{parentCommentId}/child-comments` - 자식 댓글 조회
</details>
<details>
  <summary> 예매내역 </summary>

  - `POST /api/book/seat` - 좌석 선택(동시성 체크)
  - `POST /api/book` - 예매 생성
  - `GET /api/admin/book` 예매 전체 조회
  - `GET /api/book` - 예매 상세 조회
  - `DELETE /api/book/{bookId}` - 예매 삭제
</details>

## 성능 개선 사항

## 실행 방법

### 프로젝트 실행

```bash
# 저장소 클론
git clone https://github.com/Tior931108/sixpark.git
cd sixpark

# Gradle 빌드 및 실행
./gradlew bootRun

# 서버는 기본적으로 http://localhost:8080에서 실행됩니다
```

## 프로젝트 구조

```
taskFlow
	├── common
	│   ├── config          # 설정 (QuertDsl,Redis 등)
	│   ├── entity          # BaseEntity, TokenBlackList
	│   ├── enums           # 공통 상수 관리
	│   ├── exception       # 전역 예외 처리
  │   ├── lock            # AOP를 이용한 Redis lock 로직
	│   ├── response        # 공통 응답 포멧
	│   └── security        # JWT, Security등 보안 설정     
	└── domain
	    ├── user            # 사용자/인증인가 도메인
	    ├── showinfo        # 공연 정보 도메인
	    ├── showplace       # 공연 장소 도메인
	    ├── showschedule    # 공연 스케줄 도메인
	    ├── genre           # 장르 도메인
	    ├── seat            # 좌석 도메인
	    ├── reservation     # 예매 도메인
      ├── post            # 게시글 도메인
	    └── comment         # 댓글 도메인
			※ 각 도메인은 기본적으로 controller, entity, service, repository 계층으로 분리되어 있고,
			  model 패키지에서 dto, request, response 객체들을 관리합니다
```
