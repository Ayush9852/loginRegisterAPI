package com.login.signup.loginResgisterAPI.Controller;

import com.login.signup.loginResgisterAPI.Model.User;
import com.login.signup.loginResgisterAPI.Payload.JwtRequest;
import com.login.signup.loginResgisterAPI.Payload.JwtResponse;
import com.login.signup.loginResgisterAPI.Payload.ResetPasswordRequest;
import com.login.signup.loginResgisterAPI.Security.JwtTokenUtil;
import com.login.signup.loginResgisterAPI.Service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            logger.info("Attempting to register user: {}", user.getUsername());

            // Use the isUserPresent method to check if the user already exists
            if (userDetailsService.isUserPresent(user.getUsername())) {
                logger.warn("Username already exists: {}", user.getUsername());
                return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
            }

            // Save the new user
            User savedUser = userDetailsService.save(user);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", user.getUsername(), e);
            return new ResponseEntity<>("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            logger.info("Attempting to reset password for user: {}", request.getUsername());

            // Find the user by username
            User existingUser = userDetailsService.findByUsername(request.getUsername());
            if (existingUser == null) {
                logger.warn("User not found for password reset attempt: {}", request.getUsername());
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Delete the existing user
            userDetailsService.deleteUser(existingUser);
            logger.info("User deleted successfully for username: {}", request.getUsername());

            // Create a new user object with the same details but updated password
            User newUser = new User();
            newUser.setUsername(existingUser.getUsername());
            newUser.setPassword(request.getNewPassword()); // Use plain password here
            newUser.setEmail(existingUser.getEmail());

            // Register the new user using the registration method
            ResponseEntity<?> registrationResponse = registerUser(newUser);

            // Check if registration was successful
            if (registrationResponse.getStatusCode().is2xxSuccessful()) {
                logger.info("Password reset successfully for user: {}", request.getUsername());
                return ResponseEntity.ok("Password reset successfully");
            } else {
                logger.error("Password reset failed for user: {}", request.getUsername());
                return registrationResponse; // Return the error from the registration process
            }
        } catch (Exception e) {
            logger.error("Password reset failed for user: {}", request.getUsername(), e);
            return new ResponseEntity<>("Password reset failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        User user = userDetailsService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

}
