package com.login.signup.loginResgisterAPI.Service;



import com.login.signup.loginResgisterAPI.Model.User;
import com.login.signup.loginResgisterAPI.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });


        logger.info("User found: {}", username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public User save(User user) {
        logger.info("Saving user: {}", user.getUsername());

        // Encrypt the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        logger.info("User saved successfully: {}", savedUser.getUsername());
        return savedUser;
    }


    public User findByUsername(String username) {
        logger.info("Finding user by username: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            logger.info("User found with username: {}", username);
            return userOptional.get();
        } else {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public boolean isUserPresent(String username) {
        try {
            findByUsername(username); // This will throw an exception if user is not found
            return true; // User exists
        } catch (UsernameNotFoundException e) {
            return false; // User does not exist
        }
    }

    public void deleteUser(User user) {
        logger.info("Deleting user: {}", user.getUsername());
        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getUsername());
    }

}



