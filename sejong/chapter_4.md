# Spring Chapter 4

## Keyword
- Spring Security
- 사용자 인증
    - In-Memory
    - JDBC
    - LDAP
    - Customizing
- 웹 요청 보안 처리
- 사용자 인지

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

### 2. 사용자 인증
    - XML, Java Class
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

#### 2-1. In-Memory
```java
...
@Override
public void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.inMemoryAuthentication()
    ...
}
```

#### 2-2. JDBC
```java
...

@Autowired
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .jdbcAuthentication()
        .dataSource(dataSource);
    // .usersByUsernameQuery(...)
    // .authoritiesByUsernameQuery(...)
    ...
}
```
    - 내부적으로 수행되는 Query
```java
public static final String DEF_USERS_BY_USERNAME_QUERY = 
    "SELECT username,password,enables " +
    "FROM users " +
    "WHERE username = ?";
public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = 
    "SELECT username,authority " +
    "FROM authorities " +
    "WHERE username = ?";
public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = 
    "SELECT g.id, g.group_name, ga.authority " +
    "FROM authorities g, group_members gm, group_authorities ga " +
    "WHERE gm.username = ? " +
    "AND g.id = ga.group_id " +
    "AND g.id = gm.group_id";
```

#### Password Encoding
    - BCryptPasswordEncoder
    - NoOpPasswordEncoder
    - Pbkdf2PasswordEncoder
    - SCryptPasswordEncoder
    - StandardPasswordEncoder

#### 2-3. LDAP 
```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth
        .ldapAuthentication()
        ...
}
```

#### 2-4. Customizing
    - User Class
    - JPA
```java
@Entity
@Data
...
public class User implements UserDetails {
    @Id
    private Long id;

    private final String username;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    ...
}

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {
    private UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
            User user = userRepo.findByUsername(username);
            if (user != null) {
                return user;
            }
            throw new UsernameNotFoundException("User '" + username + "' not found");
            )
        }
}

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    ...
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
            auth
                .userDetailsService(userDetailsService);
                ...
        }
    ...
}
```

### 4. 웹 요청 보안 처리하기
    - Access 설정
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .hasRole("ROLE_USER")
        .antMatchers("/","/**").permitAll();
}
```
    - HttpSecurity를 사용해서 구성할 수 있는 것
        - HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건을 구성한다.
        - 커스텀 로그인 페이지를 구성한다.
        - 사용자가 애플리케이션의 로그아웃을 할 수 있도록 한다.
        - CSRF 공격으로부터 보호하도록 구성한다.
        - p149~150 참조
    - 스프링 표현식을 사용한 인증 규칙 정의하기
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .access("hasRole('ROLE_USER') && " +
            "T(java.util.Calendar).getInstance().get("+
            "T(java.util.Calendar).DAY_OF_WEEK) == " +
            "T(java.util.Calendar).TUESDAY")
        .antMatchers("/", "/**").access("permitAll");
}
```
    - Custom Login page
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        ...
        .and()
        .formLogin()
        .loginPage("/login")
        .loginProcessingUrl("/authenticate")
        .usernameParameter("user")
        .passwordParameter("pwd")
        .and()
        .logout()
        .logoutSuccessUrl("/")
}
```
    - CSRF 방어하기
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        ...
        .and()
        .csrf()
}
```

### 5. 사용자 인지
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
