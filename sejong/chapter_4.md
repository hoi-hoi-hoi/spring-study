# Spring Chapter 4

## Keyword
- Spring Security
- In-Memory
- JDBC
- LDAP
- 웹 요청 보안 처리

---

### 1. Spring Security
    - Access, User 관리
        - Authorization
        - Authentication
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-security-test</artifactId>
    </dependency>
</dependencies>
```

### 2. Spring Security Configuration
    - XML
    - Java Class
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) thows Exception {
        http
        ...
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.
        ...
    }
}
```

### 3. In-Memory 사용자 스토어
```java
...
@Override
public void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.inMemoryAuthentication()
    ...
}
```

### 4. JDBC 사용자 스토어
```java
...

@Autowired
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .jdbcAuthentication()
        .dataSource(dataSource);
    ...
}
```

### 5. LDAP 사용자 스토어
```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
        ...
}
```

### 6. 사용자 인증 커스터 마이징
    - User class를 통해서 설정(UserDetails를 구현?)
```java
@Entity
@Data
...
public class User implements UserDetails {
    ...
}
```

### 7. 웹 요청 보안 처리하기
    - Access 설정
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.
    ...
}
```
    - HttpSecurity를 사용해서 구성할 수 있는 것
        - HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건을 구성한다.
        - 커스텀 로그인 페이지를 구성한다.
        - 사용자가 애플리케이션의 로그아웃을 할 수 있도록 한다.
        - CSRF 공격으로부터 보호하도록 구성한다.
        - p149~150 참조


### 8. 사용자 정보 얻기
    - Principal 객체를 컨트롤러 메서드에 주입한다.
```java
@PostMapping
public String processOrder(
    @Valid Order order, 
    Errors errors, 
    SessionStatus sessionStatus, 
    Principal principal){
    ...
    User user = userRepository.findByUsername(principal.getName());
    ...
}
```
    - Authentication 객체를 컨트롤러 메서드에 주입한다.
        - 보안 특정 코드만 갖으며 형 변환 필요
```java
@PostMapping
public String processOrder(
    @Valid Order order, 
    Errors errors, 
    SessionStatus sessionStatus, 
    Authentication authentication){
    ...
    User user = (User) authentication.getPrincipal();
    ...
}
```
    - SecurityContextHolder를 사용해서 보안 컨텍스트를 얻는다.
        - applcation 어디서든 사용 가능
```java
Authentication authentication =
    SecurityContextHolder.getContext().getAuthentication();
User user = (User) authentication.getPrincipal();
```
    - @AuthenticationPrincipal 애노테이션을 메서드에 지정한다.
        - 보안 특정 코드만 갖으며, 형 변환 필요 없음
```java
@PostMapping
public String processOrder(
    @Valid Order order, 
    Errors errors, 
    SessionStatus sessionStatus, 
    @AuthenticationPrincipal User user){
    ...
    user.
    ...
}
```
