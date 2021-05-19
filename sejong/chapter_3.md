# Spring Chapter 3

## Keyword
- Data persistence
- JDBC
- Spring Data

---

### 1. Data persistence
    - Data를 저장하고 지속적으로 유지
    - Database 사용

### 2. JDBC(Java Database Connectivity)
    - 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API
    - 자바에서 데이터베이스에 자료를 쿼리하거나 업데이트하는 방법을 제공
    
```java
public interface IngredientRepository {
    Iterable<Ingredient> findAll();
    Ingredient findById(String id);
    Ingredient save(Ingredient ingredient);
}
```
```java
@Repository
public class JdbcIngredientRepository implements IngredientRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public JdbcIngredientRepository(jdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        return jdbc.query("select id, name, type from Ingredient",
        this::mapRowToIngredient);
    }

    @Override
    public Ingredient findById(String id) {
        return jdbc.queryForObject(
            "select id, name, type from Ingredient where id=?",
            this::mapRowToIngredient, id);
        )
    }

    @Override
    public Ingredient findById(String id) {
        return jdbc.queryForObject(
            "select id, name, type from Ingredient where id=?",
            this::mapRowToIngredient, id);
        )
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum)
     throws SQLException {
         return new Ingredient(
             rs.getString("id"),
             rs.getString("name"),
             Ingredient.Type.valueOf(rs.getString("type")));
     }
}
```

### 3. Spring Data
    - Spring Data JPA: 관계형 데이터베이스의 JPA persistence
    - Spring Data MongoDB
    - Spring Data Neo4
    - Spring Data Redis
    - Spring Data Cassandra

```java
@Data
@Entity // JPA 객체로 선언하기 위한 annotation
public class User {
    @Id
    @GenerateValue(strategy=GenerationType.AUTO)
    private final String id;
    @NotNull
    private final String name;
}
```
```java
// CrudRepository<개체 타입, 개체 ID 속성의 타입>
public interface UserRepository 
    extends CrudRepository<User, String> {}
```