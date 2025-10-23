package com.example.Library_Management.configs;

import com.example.Library_Management.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // inject your JWT filter here



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // ✅ Enable CORS support
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        // Open endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // ✅ Allow preflight requests from browser
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()



                        // Books
                        .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("USER", "ADMIN") // both can view
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN") // only admin
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")  // only admin
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN") // only admin

                        // Members
                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/members/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
// Loans (User)
                                .requestMatchers(HttpMethod.POST, "/api/loans/borrow").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/api/loans/*/return").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/loans/mine").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/loans/overdue/mine").hasAnyRole("USER", "ADMIN") // ✅ added



// Admin only
                                .requestMatchers("/api/loans/**").hasRole("ADMIN")


                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // Stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Register JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
