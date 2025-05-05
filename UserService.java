package com.devendpoint.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devendpoint.config.JwtUtil;
import com.devendpoint.dto.LoginRequest;
import com.devendpoint.dto.RegisterRequest;
import com.devendpoint.dto.UserDto;
import com.devendpoint.model.User;
import com.devendpoint.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    

    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already taken.";
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already registered.";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(hashedPassword);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRole(0);
        user.setWriteAccess(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);
        
        emailService.sendUserDetailsEmail(user.getEmail(),user.getUsername(),request.getPassword());

        return "User registered successfully!";
    }

    public String login(LoginRequest request) {
//    	System.out.println("in Login");
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

//        System.out.println("user Password-"+user.getPassword());
//        System.out.println("request Pass-"+request.getPassword());
//        String pass = passwordEncoder.encode(request.getPassword());
//        System.out.println(pass);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

//        return "Login successful!";
        
        // Generate JWT
        String token = jwtUtil.generateToken(user.getUsername());
        return "Login successfull"+"\n"+token;
    }
    
    public String updateUser(RegisterRequest request) {

            if (!userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Username not exist.";
        }

        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRole(0);
        user.setWriteAccess(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        emailService.sendUserDetailsEmail(user.getEmail(),user.getUsername(),request.getPassword());

        return "User updated successfully!";
    }
    
    public UserDto getProfile(LoginRequest request ) {
    	User user = userRepository.findByUsername(request.getUsername()).get();
        UserDto dto = new UserDto();
    
        dto.setEmail(user.getEmail());
        dto.setId(user.getId());
        dto.setUsername(request.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
//        dto.setPassword(request.getPassword());
        dto.setEnabled(true);
        dto.setEmailVerified(false);
        dto.setRole(0);
        dto.setWriteAccess(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setLastLoginAt(LocalDateTime.now());
        
        return dto;
    }
    
//    public String forgetPass(LoginRequest request) {
//    	
//    	User user = userRepository.findByUsername(request.getUsername());
//    	String newPass = request.getPassword();
//    	user.setPassword(newPass);
//    	return "Password updated successfully";
//                
//
    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        User user = userOptional.orElseThrow(() ->
            new UsernameNotFoundException("User not found with username: " + username));

        System.out.println("Request Received to user for login: " + username);

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Example if your User entity has roles: 
         authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }


}
