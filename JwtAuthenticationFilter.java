package com.devendpoint.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devendpoint.service.UserService;

import java.io.IOException;

@Component
	public class JwtAuthenticationFilter extends OncePerRequestFilter {

	    private final JwtUtil jwtUtil;
	    private final UserService userService;

	    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
	        this.jwtUtil = jwtUtil;
	        this.userService = userService;
	    }

	    @Override
	    protected void doFilterInternal(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain filterChain) throws ServletException, IOException {

	        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

	        if (header != null && header.startsWith("Bearer ")) {
	            String token = header.substring(7);
	            if (jwtUtil.validateToken(token)) {
	                String username = jwtUtil.extractUsername(token);
	                UserDetails userDetails = userService.loadUserByUsername(username);
	                UsernamePasswordAuthenticationToken authToken =
	                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	                System.out.println("User authenticated: " + username + " with roles: " + userDetails.getAuthorities());

	            } else {
	                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
	                return;
	            }
	        } else if (requiresAuth(request)) {
	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT Token");
	            return;
	        }

	        filterChain.doFilter(request, response);	
	    }


	

    private boolean requiresAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.contains("/api/v1/auth/"); // All routes except /auth/* require token
    }
}
