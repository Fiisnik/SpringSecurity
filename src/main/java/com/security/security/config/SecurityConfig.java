package com.security.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;


import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(CsrfConfigurer::disable) // Désactive CSRF
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/auth/register", "/auth/login", "/categories/getAll")
                .permitAll() // Autorise les endpoints publics
                .anyRequest()
                .authenticated() // Authentification nécessaire pour le reste
        )
        .sessionManagement(sessionManagement ->
            sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Pas de sessions HTTP
        )
        .authenticationProvider(authenticationProvider) // Ajoute le provider d'authentification
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre JWT avant UsernamePasswordAuthenticationFilter

    return http.build();
  }


}