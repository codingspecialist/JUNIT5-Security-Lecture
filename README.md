# JUNIT & Security

### 이슈1 (해결)
- 입금, 출금, 입출금 내역보기 (하나로 통일해서 동적쿼리 쓸지 고민중)
- 프론트에 입금, 출금 전체 내역에 보내주고 선택해서 쓰게 해주는게 맞나?
- 아니면 프론트에 요청시마다 입금내역, 출금내역, 입출금내역을 전달해주는 것이 맞나?
- 은행 어플 보면 각 각의 API가 따로 있긴하다.
- 위 이슈는 동적쿼리로 정리함 

### 이슈2 (해결)
- Transper, Deposit, Withdraw 요청시에 주소에 필요한 값 받고, DTO에서 
- 값 빼고, DTO 통일해서 하나의 컨트롤러로 처리하기
- 위 이슈는 DTO 통일 불가능함. null값이 들어와서 - 기존 유지함. 

### 이슈3 (해결)
- toString()으로 문자열 캐스팅 하지말자. Lazy Loading됨.

### 이슈4
- Validation 체크
  
### 이슈5 (해결)
- AOP 권한 체크

### 이슈6 (해결)
- Repository, Service 테스트 해야함
- 
### 이슈7 (해결)
- @DataJpaTest에서 truncate하면 auto_increment 초기화 안됨.
- Alter 명령어로 직접 increment 초기화함. 

### 이슈8 (해결)
- @Slf4j 해당 어노테이션으로 빌드하면 빌드 실패함.
- 아래방식으로 해결
```java
private final Logger log = LoggerFactory.getLogger(getClass());
```

### 이슈9 (해결)
- 계좌 비밀번호 확인 처리 해야함
- 0원 이체, 0원 출금, 0원 입금 불가능 처리 해야함.
- 잔액 유효성 검사 메서드 분리
- 계좌주인 이름 필요

### 이슈10 (해결)
- 계좌 개설시에는 무조건 1000원 들어가 있어야 함.

### 이슈11 (해결)
- DTO에 화면에는 필요 없지만, Test 검증을 위해 데이터를 추가해야 하는가?
- 하지말자!!!!!!!!!!!!!!!

### 이슈12
- LocalDateTime 문제 (getter 어떻게 할것인지)
- 서비스 테스트시에 id값 할당 어떻게 할지 (setData() 메서드로 시간과 ID를 함께 반영하는 것이 어떨까?)
- 서비스쪽 조회 로직 마지막으로 점검하기
 
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

```sql
create database metadb;
use metadb;

create table users (
       id bigint auto_increment,
        created_at timestamp not null,
        updated_at timestamp not null,
        email varchar(255) not null,
        password varchar(255) not null,
        role varchar(255) not null,
        username varchar(255) not null unique,
        primary key (id)
);
create table account (
       id bigint auto_increment,
        created_at timestamp not null,
        updated_at timestamp not null,
        balance bigint not null,
        number bigint not null unique,
        owner_name varchar(255) not null,
        password varchar(255) not null,
        user_id bigint,
        is_use boolean not null,
        primary key (id)
);

create table transaction (
       id bigint auto_increment,
        created_at timestamp not null,
        updated_at timestamp not null,
        amount bigint not null,
        deposit_account_balance bigint,
        gubun varchar(255) not null,
        withdraw_account_balance bigint,
        deposit_account_id bigint,
        withdraw_account_id bigint,
        primary key (id)
);
```

### 테스트 더미 데이터 (순수 객체)
```java
User ssarUser = newUser(1L, "ssar");
                User cosUser = newUser(2L, "cos");
                User adminUser = newUser(3L, "admin");
                List<User> users = Arrays.asList(ssarUser, cosUser, adminUser);
                Account ssarAccount1 = newAccount(1L, 1111L, ssarUser);
                Account ssarAccount2 = newAccount(2L, 2222L, ssarUser);
                Account cosAccount1 = newAccount(3L, 3333L, cosUser);
                List<Account> accounts = Arrays.asList(ssarAccount1, ssarAccount2, cosAccount1);
                Transaction withdrawTransaction1 = newWithdrawTransaction(1L, ssarAccount1);
                Transaction withdrawTransaction2 = newWithdrawTransaction(2L, ssarAccount1);
                Transaction depositTransaction1 = newDepositTransaction(3L, ssarAccount1);
                Transaction transferTransaction1 = newTransferTransaction(4L, ssarAccount1, cosAccount1);
                List<Transaction> transactions = Arrays.asList(withdrawTransaction1, withdrawTransaction2,
                                depositTransaction1, transferTransaction1);
```

### 테스트 더미 데이터 (영속화된 객체)
```java
              User ssarUser = userRepository.save(newUser(1L, "ssar"));
                User cosUser = userRepository.save(newUser(2L, "cos"));
                User adminUser = userRepository.save(newUser(3L, "admin"));
                Account ssarAccount1 = accountRepository.save(newAccount(1L, 1111L, ssarUser));
                Account ssarAccount2 = accountRepository.save(newAccount(2L, 2222L, ssarUser));
                Account cosAccount1 = accountRepository.save(newAccount(3L, 3333L, cosUser));
                Transaction withdrawTransaction1 = transactionRepository.save(newWithdrawTransaction(1L, ssarAccount1));
                Transaction withdrawTransaction2 = transactionRepository.save(newWithdrawTransaction(2L, ssarAccount1));
                Transaction depositTransaction1 = transactionRepository.save(newDepositTransaction(3L, ssarAccount1));
                Transaction transferTransaction1 = transactionRepository
                                .save(newTransferTransaction(4L, ssarAccount1, cosAccount1));
```

### 참고
```txt
    // @WithMockUser // 기본값 username=user, password=password role=ROLE_USER
    // @WithMockUser(username = "ssar", password = "1234", roles = "CUSTOMER")
    // https://velog.io/@rmswjdtn/Spring-SecuritywithUserDetails-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0
    // SecurityContext는 default로 TestExecutionListener.beforeTestMethod로 설정이 되어있습니다.
    // 따라서 @BeforeAll, @BeforeEach 실행전에 WithUserDetails가 실행되어서, DB에 User가 생기기전에 실행됨
    // setupBefore = TestExecutionEvent.TEST_EXECUTION 이것을 사용하자
```