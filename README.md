# JUNIT & Security

### 프로젝트명
- bank

### 사용기술
- Security
- JPA
- H2
- AOP

### 테이블 스키마
- User 
- Account
- Transaction

### 할일 목록
- User 테스트
- Product 테스트
- Purchase 테스트

### 시나리오
- 
-
-

### 참고
```txt
    // @WithMockUser // 기본값 username=user, password=password role=ROLE_USER
    // @WithMockUser(username = "ssar", password = "1234", roles = "CUSTOMER")
    // https://velog.io/@rmswjdtn/Spring-SecuritywithUserDetails-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0
    // SecurityContext는 default로 TestExecutionListener.beforeTestMethod로 설정이 되어있습니다.
    // 따라서 @BeforeAll, @BeforeEach 실행전에 WithUserDetails가 실행되어서, DB에 User가 생기기전에 실행됨
    // setupBefore = TestExecutionEvent.TEST_EXECUTION 이것을 사용하자
```