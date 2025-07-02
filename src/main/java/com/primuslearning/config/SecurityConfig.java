package com.primuslearning.config;

import com.primuslearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Arrays;

/**
 * Security configuration for the application
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Order(2)
public class SecurityConfig {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    
    @Value("${security.headers.content-security-policy:default-src 'self'}")
    private String contentSecurityPolicy;
    
    @Value("${security.headers.referrer-policy:same-origin}")
    private String referrerPolicy;
    
    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder, Environment environment) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure H2 console access only in dev profile
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        http
            .authorizeHttpRequests(authz -> {
                // Public resources
                authz.requestMatchers("/", "/home", "/register", "/login", "/css/**", "/js/**", "/images/**", 
                               "/webjars/**", "/favicon.ico", "/error", "/error/**").permitAll();
                
                // H2 console access only in dev profile
                if (isDevProfile) {
                    authz.requestMatchers("/h2-console/**").permitAll();
                }
                
                // Admin-only endpoints
                authz.requestMatchers("/admin/**").hasRole("ADMIN");
                
                // User and admin endpoints
                authz.requestMatchers("/learning-paths/create", "/learning-paths/edit/**", 
                               "/learning-paths/delete/**", "/profile/**").hasAnyRole("USER", "ADMIN");
                
                // All other requests require authentication
                authz.anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login?invalid=true")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> {
                if (isDevProfile) {
                    csrf.ignoringRequestMatchers("/h2-console/**");
                }
            })
            .headers(headers -> {
                // Configure security headers
                headers.contentSecurityPolicy(csp -> csp.policyDirectives(contentSecurityPolicy));
                headers.referrerPolicy(referrer -> referrer.policy(referrerPolicy));
                headers.xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
                headers.contentTypeOptions(contentType -> {});
                
                // Allow H2 console in dev profile
                if (isDevProfile) {
                    headers.frameOptions(frameOptions -> frameOptions.sameOrigin());
                } else {
                    headers.frameOptions(frameOptions -> frameOptions.deny());
                }
            });
            
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectURL = request.getContextPath();
            
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectURL += "/admin/dashboard";
            } else {
                redirectURL += "/dashboard";
            }
            
            response.sendRedirect(redirectURL);
        };
    }
}
