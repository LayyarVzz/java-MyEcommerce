package com.example.myecommerce.config;

import com.example.myecommerce.service.UserService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.RoleHomeResolver;
import com.example.myecommerce.util.RequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final UserService userService;
    private final UserActivityService userActivityService;
    private final RoleHomeResolver roleHomeResolver;

    // 构造器注入 UserService
    public SecurityConfig(UserService userService,
                          UserActivityService userActivityService,
                          RoleHomeResolver roleHomeResolver) {
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.roleHomeResolver = roleHomeResolver;
    }


    // 安全过滤链：配置登录、权限、退出等规则
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF（新手简化，生产环境需开启）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置请求权限
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/products", "/products/**", "/register", "/login", "/css/**", "/js/**", "/upload/**").permitAll()
                        .requestMatchers("/admin/dashboard").hasRole("ADMIN")
                        .requestMatchers("/admin/customers/**").hasRole("ADMIN")
                        .requestMatchers("/admin/products", "/admin/products/**", "/admin/orders", "/admin/orders/**").denyAll()
                        .requestMatchers("/admin/reports/**", "/admin/activities/**").hasRole("ADMIN")
                        .requestMatchers("/sales/**").hasRole("SALES")
                        .requestMatchers("/activities/product-browse-duration").hasRole("USER")
                        .requestMatchers("/orders/**").hasRole("USER")
                        .requestMatchers("/addresses/**").hasRole("USER")
                        .requestMatchers("/cart/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                // 配置登录页面
                .formLogin(form -> form
                        .loginPage("/login") // 自定义登录页路径
                        .successHandler((request, response, authentication) -> {
                            userActivityService.recordLogin(authentication.getName(), RequestUtils.getClientIp(request));
                            response.sendRedirect(roleHomeResolver.resolveHomeUrl(authentication));
                        })
                        .permitAll()
                )
                // 配置退出登录
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // 退出后跳回登录页
                        .clearAuthentication(true)     // 清除认证信息
                        .invalidateHttpSession(true)   // 使会话失效
                        .permitAll()
                )
                // 启用 Remember Me 功能
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret") // 用于生成 remember-me token 的密钥
                        .tokenValiditySeconds(60 * 60 * 24) // token 有效期，这里是24小时
                        .userDetailsService(userService) // 使用注入的 userService
                );

        return http.build();
    }

}
