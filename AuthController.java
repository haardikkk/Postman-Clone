package com.devendpoint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devendpoint.dto.LoginRequest;
import com.devendpoint.dto.RegisterRequest;
import com.devendpoint.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	 private final UserService userService;

	    // Constructor injection (without Lombok)
	    public AuthController(UserService userService) {
	        this.userService = userService;
	    }

	@PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request)); // ✅ CORRECT
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
