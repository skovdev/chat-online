package local.chatonline.auth.service.impl;

import local.chatonline.auth.config.JwtTokenProvider;

import local.chatonline.auth.exception.EmailAlreadyExistsException;
import local.chatonline.auth.exception.UsernameAlreadyExistsException;

import local.chatonline.auth.model.Role;
import local.chatonline.auth.model.User;

import local.chatonline.auth.repository.UserRepository;

import local.chatonline.auth.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    JwtTokenProvider jwtTokenProvider;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;

    @Override
    public String loginUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtTokenProvider.generationToken(authentication);
    }

    @Override
    public User registerUser(User user, Role role) {

        log.warn("registering user: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("username {} already exists.", user.getUsername());
            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists", user.getUsername()));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());
            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists", user.getEmail()));
        }

        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>() {{
            add(role);
        }});

        return userRepository.save(user);

    }

    @Override
    public List<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(String id) {
        log.info("retrieving user {}", id);
        return userRepository.findById(id);
    }
}
