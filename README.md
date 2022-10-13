# JUNIT & Security

### 프로젝트명
- market

### 사용기술
- Security
- JPA
- H2
- AOP

### 테이블 스키마
- User (Seller, Buyer)
- Product
- Purchase (User.id, Product.id, createdAt, updatedAt)

### 할일 목록
- User 테스트
- Product 테스트
- Purchase 테스트

### EntityManager와 JpaRepository 통합
- 동적쿼리 작성
- 코드 일관성 유지]

### 시나리오
- Seller 회원가입
- 로그인(Seller)
- Product 등록
- Product 수정
- Product 삭제


- User 회원가입
- 로그인(User)
- Purchase 등록 (Product 구매)
- Purchase 업데이트(상태변경) (Product 취소)