# 🏦 HR Bank

**HR Bank**는 기업의 인적 자원을 안전하고 효율적으로 관리하기 위한 Open EMS(Enterprise Management System)입니다.  
대량 데이터를 안정적으로 처리할 수 있는 Batch 시스템을 기반으로, 부서·직원·파일·이력·백업·대시보드를 효율적으로 운영할 수 있습니다.  

---

## 👥 팀 소개 (Team Members & R&R)

| 기능 영역              | 담당자       | 역할 설명 |
|------------------------|-------------|-----------|
| 부서 관리              | 주세훈, 정서연 | 부서 등록/수정/삭제/조회 |
| 직원 관리              | 변우혁, 김규섭 | 직원 CRUD, 통계 |
| **파일 관리**          | 정건진   | 파일 업로드/다운로드/메타 정보 |
| 직원 수정 이력 관리    | (미정)       | 변경 이력 기록 및 조회 |
| 데이터 백업 관리       | (미정)       | 배치 기반 백업 및 이력 관리 |
| 대시보드 관리          | (미정)       | 직원/부서 통계 시각화 |

---

## 🚀 주요 기능 (Features)

- **부서 관리**: 부서 등록, 수정, 삭제, 목록 조회, 상세 조회
- **직원 관리**: 직원 등록, 수정, 삭제(퇴사 처리), 목록 조회, 상세 조회, 통계
- **파일 관리**: 파일 메타 정보 저장, 다운로드
- **이력 관리**: 직원 정보 변경 이력 등록, 조회, 상세 diff 조회
- **데이터 백업 관리**: 데이터 백업 생성, 자동 배치 백업, 백업 이력 조회
- **대시보드 관리**: 직원 수, 입사자, 분포, 백업 현황 시각화

👉 [Swagger API 문서](http://sprint-project-1196140422.ap-northeast-2.elb.amazonaws.com/sb/hrbank/api/swagger-ui/index.html)

---

## 📑 API Endpoints (요약)

### 직원(Employee)
- `GET /api/employees` — 직원 목록 조회
- `POST /api/employees` — 신규 직원 등록
- `GET /api/employees/{id}` — 직원 상세 조회
- `PATCH /api/employees/{id}` — 직원 수정
- `DELETE /api/employees/{id}` — 직원 삭제(퇴사 처리)

### 부서(Department)
- `GET /api/departments` — 부서 목록 조회
- `POST /api/departments` — 부서 등록
- `GET /api/departments/{id}` — 부서 상세 조회
- `PATCH /api/departments/{id}` — 부서 수정
- `DELETE /api/departments/{id}` — 부서 삭제

### 파일(File)
- `POST /api/files` — 파일 업로드
- `GET /api/files/{id}/meta` — 파일 메타 조회
- `GET /api/files/{id}/download` — 파일 다운로드

### 이력(Change Log)
- `GET /api/change-logs` — 이력 목록 조회
- `GET /api/change-logs/{id}/diffs` — 특정 이력 변경 상세 조회

### 데이터 백업(Backup)
- `GET /api/backups` — 백업 이력 조회
- `POST /api/backups` — 데이터 백업 생성
- `GET /api/backups/latest` — 최근 백업 조회

---

## 🛠 기술 스택 (Tech Stack)

### Backend
- Java 17
- Spring Boot
- Spring Web (REST API)
- Spring Data JPA
- Validation (Bean Validation)
- Spring Boot Actuator
- MapStruct (Entity ↔ DTO 매핑)

### Database
- PostgreSQL
- PostgreSQL JDBC Driver

### Infra / Deployment
- Railway.io (클라우드 배포 & DB 호스팅)

### Frontend & API Docs
- HTML / CSS / JavaScript (제공된 정적 리소스)
- Swagger UI (Springdoc OpenAPI 2.8.9)

### Utilities / Libraries
- Lombok
- Logging (Spring + Hibernate SQL 로깅)
- JUnit 5 (Spring Boot Starter Test, Mockito, AssertJ 포함)

---

## 📂 프로젝트 구조 (Project Structure)

```

sb05-hrbank-team02
┣ .gradle
┣ .idea
┣ build.gradle
┣ gradlew
┣ gradlew\.bat
┣ settings.gradle
┣ src
┃ ┣ main
┃ ┃ ┣ java
┃ ┃ ┃ ┗ com
┃ ┃ ┃   ┗ sprint
┃ ┃ ┃     ┗ project
┃ ┃ ┃       ┗ hrbank
┃ ┃ ┃         ┣ HrbankApplication.java
┃ ┃ ┃         ┣ controller
┃ ┃ ┃         ┣ dto
┃ ┃ ┃         ┣ entity
┃ ┃ ┃         ┣ exception
┃ ┃ ┃         ┣ repository
┃ ┃ ┃         ┗ service
┃ ┃ ┣ resources
┃ ┃ ┃ ┣ application.yml
┃ ┃ ┃ ┣ static
┃ ┃ ┃ ┗ templates
┃ ┣ test
┃ ┃ ┗ java
┃ ┃   ┗ com
┃ ┃     ┗ sprint
┃ ┃       ┗ project
┃ ┃         ┗ hrbank
┃ ┃           ┗ HrbankApplicationTests.java

```

---

## ⚙️ 실행 방법 (Getting Started)

---

## 📋 팀 규칙 (Team Rules)

* 2시간 이상 해결되지 않는 문제는 반드시 팀원과 공유
* 개인 일정은 팀장에게 사전 공유
* 아침 9시 (또는 주강사님 공지 이후) **데일리 스크럼** 진행

  * 어제 한 일 / 오늘 할 일 / 막힌 부분 공유
  * Notion에 기록 후 공유
* 작업 시작 전:

  * `git pull origin main` → 작업 브랜치 checkout → 최신 main merge 후 개발 시작

---

## ⚙️ 개발 규칙 (Development Rules)

* **To-Do 관리**

  * Notion 사용
  * 매일 스크럼 이후 오늘 할 일 Issue 등록
    
* **R\&R 분배**

  * 기능별 담당자 + 문서화 + 리뷰 담당자 명확히 구분
    
* **문서화**

  * 모든 결정/구현사항 Notion 기록
    
* **PR 규칙**
  * PR 템플릿에 맞춰 작성 (Github PR Template 기능 활용)
  * 최소 1명 이상 승인 필요 (Github codeowners 기능 활용)
  * 리뷰어는 24시간 이내 리뷰 작성
    
* **멘토링 준비**

  * 팀별 질문 사전 정리 및 공유

---

## 📝 Git Commit 컨벤션

```
<type>(<scope>): <subject>

<body>

<footer>
```

* **Type**

  * feat: 새로운 기능
  * fix: 버그 수정
  * docs: 문서 수정
  * style: 포맷팅/공백 (로직 영향 없음)
  * refactor: 기능 변경 없는 구조 개선
  * test: 테스트 코드
  * chore: 빌드/설정/라이브러리 변경
  * ci: CI/CD 관련
* **Scope**: 사용하지 않음
* **Subject**: 50자 이내, 동사 현재형, 마침표 X
* **Body**: 무엇을/왜 변경했는지 상세 설명
* **Footer**: Issue 번호, Breaking Change, PR 참조

---

## 🌱 브랜치 전략 (GitHub Flow)

* main 브랜치는 항상 배포 가능한 상태 유지
* 기능 개발은 `feat/기능명`, 버그 수정은 `fix/이슈번호`
* 브랜치명은 소문자 + 하이픈(`-`)

  * 예: `feat/add-employee-api`, `fix/login-error`
* 작업 완료 후 Pull Request 생성 → 코드 리뷰 → 승인 후 main merge
* Branch Protection Rule

  * main 직접 push 불가
  * 승인 없는 merge 불가
  * merge 후 브랜치 자동 삭제

---

## 🧩 코드 컨벤션 (Google Java Style)

* **네이밍**

  * 클래스: PascalCase
  * 메서드/변수: camelCase
  * 상수: UPPER\_CASE
* **들여쓰기**: 2칸
* **한 줄 길이**: 최대 100자
* **괄호**

  * `if (` 처럼 키워드 뒤에는 공백
  * 중괄호 `{` 는 같은 줄에 작성
* **주석**

  * Javadoc (`/** … */`)
  * “무엇을” 보다는 “왜”를 설명
* **빈 줄**

  * 메서드 사이, 논리 블록 사이 1줄 공백

---

## 📦 결과물 (Deliverables)

* 발표자료 (PDF)
* 시연영상 (MP4)
* 팀 GitHub 레포지토리
* Railway 배포 링크
* 팀 협업 문서 (Notion 등)
* 팀원별 개발 리포트

---
