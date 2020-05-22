## 1. 资源放行

SpringSecurity当然也会拦截静态资源，所以我们可以手动放行

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("static/**");
    }
}
```









## 2. 前后端分离



#### 2.1 JackJson

 Springboot自带JackJson，所以不用再添加其他依赖了

```java
// ojbect <----> json
Object object = new Object();
ObjectMapper mapper = new ObjectMapper();
String json =  mapper.writeValueAsString(object);
Object o = mapper.readValue(json,object.getClass());
```



#### 2.2 后端处理

SpringSecurity有自带的处理器可以设置

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                
            // 登录
            .and().formLogin() 
            .successHandler((req, resp, authentication) -> {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                out.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal()));
                out.flush();
                out.close();
            })
            .failureHandler((req, resp, e) -> {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                out.write(new ObjectMapper().writeValueAsString(e.getMessage()));
                out.write("用户名或密码错误");
                if(e instanceof LockedException){
                    out.write("账户被锁定");
                }
                out.flush();
                out.close();
            })

            // 注销成功
            .and().logout()
            .logoutSuccessHandler((req, resp, authentication)->{
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                out.write("注销成功");
                out.flush();
                out.close();
            })

            // 未认证处理
            .and()
            .exceptionHandling().authenticationEntryPoint((req, resp, e) -> {
            PrintWriter out = resp.getWriter();
            out.write("尚未登录，请登录");
            out.flush();
            out.close();
        })
    }
}
```









## 3. 添加验证码



#### 3.1 自定义过滤器

```java
@Component
public class VerifyCodeFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        if (request.getMethod().equalsIgnoreCase("POST")) && 
           (request.getServletPath().equalsIgnoreCase("/login")) {
            // 验证码验证
            String code = request.getParameter("code");
            String verifyCode = (String) request.getSession().getAttribute("verifyCode");
            if (StringUtils.isEmpty(code))
                throw new AuthenticationServiceException("验证码不能为空!");
            if (!verifyCode.equalsIgnoreCase(code)) {
                throw new AuthenticationServiceException("验证码错误!");
            }
        }
        chain.doFilter(request, response);
    }
}
```



#### 3.2 添加配置

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    VerifyCodeFilter verifyCodeFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        // 关键在这里添加拦截器
        http.addFilterBefore(verifyCodeFilter, UsernamePasswordAuthenticationFilter.class);
       
        http.authorizeRequests()
                .formLogin()
    }
}
```









## 4. 角色继承

```java
@Bean
RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy = "ROLE_dba > ROLE_admin \n ROLE_admin > ROLE_user";
    roleHierarchy.setHierarchy(hierarchy);
    return roleHierarchy;
}
```









## 5. 连接数据库

* 实现UserDetail接口的对象可作为认证的数据源

* 实现UserDetailsService接口方法loadUserByUsername的类可返回UserDetail给SpringSecurity

* 重写配置类中configure(AuthenticationManagerBuilder auth)方法即可



**Entity类**

```java
public class User implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private List<Role> roles;
    private boolean enabled;

    private String email;
    private String userface;
    private Timestamp regTime;

    public Timestamp getRegTime() {
        return regTime;
    }

    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public String getUserface() {
        return userface;
    }

    public void setUserface(String userface) {
        this.userface = userface;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    // 下面的是实现的方法
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return authorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
```



**对应Server层**

```java
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RolesMapper rolesMapper;

    // 实现UserDetailsService的方法
    // 根据用户用户名查找用户信息
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userMapper.loadUserByUsername(username);
        if (user == null) {
            return new User();
        }
        //查询用户的角色信息，并返回存入user中
        List<Role> roles = rolesMapper.getRolesByUid(user.getId());
        user.setRoles(roles);
        return user;
    }
}
```



**配置类**

```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   
    @Autowired
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        
      // auth.userDetailsService(userService).passwordEncoder(new PasswordEncoder());
        
        auth.userDetailsService(userService).passwordEncoder(
            
        new PasswordEncoder() {
            
            @Override
            public String encode(CharSequence charSequence) {
               return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
               return s.equals(DigestUtils.md5DigestAsHex(charSequence.toString().getBytes()));
            }
        });
    }
}
```

