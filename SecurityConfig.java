package com.devendpoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devendpoint.service.UserService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
	
	private final JwtUtil jwtUtil;
	
	public SecurityConfig(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil, UserService userService) throws Exception {
	    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, userService);

	    http.csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/v1/auth/**").permitAll()
	            .requestMatchers("/api/v1/user/**").hasRole("USER")
	            .anyRequest().authenticated())
	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

}
