# Spring Chapter 2

## Keyword
- Web Application
- Spring MVC
    - Model
    - View
    - Controller

---

### 1. Domain
    - 영역, 집합을 의미
    - Spring에서 Model class를 통해 정의
        - Lombok library 활용
    
    * DDD(Domain-Driven Design)

```java
@Data
public class User {
    private final String id;
    private final String name;
}
```

### 2. Controller
    - HTTP Request를 처리
        - Return할 HTML page를 View에 요청
        - REST 형태의 응답에 데이터를 추가

```java
@Sl4j // for logging
@Controller
@RequestMapping("/user")
public class UserController {
    // @RequestMapping(method=RequestMethod.GET) 가능
    @GetMapping
    public String showUser(Model model) {
        // ...
        return "user";
    }
}
```

### 3. View
    - HTML page 정의
    - Spring에서 View를 정의하는 여러가지 방법들
        - JSP
        - Thymeleaf
        - FreeMarker
        - Mustache
        - Groovy기반의 템플릿

---

### 유효성 검사
```java
@Data
public class Order {
    @NotBlank(message="Name is required")
    private String deliveryName;
    @CreditCardNumber(message="Not a valid credit card number")
    private String deliverZip;
    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
            message="Must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;
}
```
```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors) {
    if (errors.hasErrors()) {
        return "orderForm";
    }
    return "redirect:/";
}
```

### View controller
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```