package com.devendpoint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devendpoint.dto.LoginRequest;
import com.devendpoint.dto.RegisterRequest;
import com.devendpoint.dto.UserDto;
import com.devendpoint.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	
	 private final UserService userService;	

	    // Constructor injection (without Lombok)
	    public UserController(UserService userService) {
	        this.userService = userService;
	    }

	@PostMapping("/updateUser")
 public ResponseEntity<String> updateUser(@RequestBody RegisterRequest request, @RequestHeader String token) {
     return ResponseEntity.ok(userService.updateUser(request)); // ✅ CORRECT
 }
	
	@PostMapping("/getProfile")
	 public ResponseEntity<UserDto> getProfile(@RequestBody LoginRequest request) {
	     return ResponseEntity.ok(userService.getProfile(request)); // ✅ CORRECT
	 }
	
}
