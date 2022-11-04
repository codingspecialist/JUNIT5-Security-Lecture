# JUNIT & Security

### 이슈정리
- 입금, 출금, 입출금 내역보기 (하나로 통일해서 동적쿼리 쓸지 고민중)
- 프론트에 입금, 출금 전체 내역에 보내주고 선택해서 쓰게 해주는게 맞나?
- 아니면 프론트에 요청시마다 입금내역, 출금내역, 입출금내역을 전달해주는 것이 맞나?
- 은행 어플 보면 각 각의 API가 따로 있긴하다.

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

### 참고
```txt
    // @WithMockUser // 기본값 username=user, password=password role=ROLE_USER
    // @WithMockUser(username = "ssar", password = "1234", roles = "CUSTOMER")
    // https://velog.io/@rmswjdtn/Spring-SecuritywithUserDetails-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0
    // SecurityContext는 default로 TestExecutionListener.beforeTestMethod로 설정이 되어있습니다.
    // 따라서 @BeforeAll, @BeforeEach 실행전에 WithUserDetails가 실행되어서, DB에 User가 생기기전에 실행됨
    // setupBefore = TestExecutionEvent.TEST_EXECUTION 이것을 사용하자
```