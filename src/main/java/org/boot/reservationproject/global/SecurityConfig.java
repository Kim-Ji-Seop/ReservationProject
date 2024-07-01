package org.boot.reservationproject.global;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.global.jwt.JwtAuthenticationFilter;
import org.boot.reservationproject.global.jwt.JwtTokenProvider;
import org.boot.reservationproject.global.redis.RedisDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final RedisDao redisDao;
  private final JwtTokenProvider jwtTokenProvider;
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder(); // 단방향 해쉬
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").permitAll())
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,redisDao)
            ,UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }
}
