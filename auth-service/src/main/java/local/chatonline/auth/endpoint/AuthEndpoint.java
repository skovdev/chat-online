package local.chatonline.auth.endpoint;

import local.chatonline.auth.exception.BadRequestException;
import local.chatonline.auth.exception.EmailAlreadyExistsException;
import local.chatonline.auth.exception.UsernameAlreadyExistsException;

import local.chatonline.auth.model.Profile;
import local.chatonline.auth.model.Role;
import local.chatonline.auth.model.User;

import local.chatonline.auth.model.http.request.LoginRequest;
import local.chatonline.auth.model.http.request.SignUpRequest;

import local.chatonline.auth.model.http.response.ApiResponse;
import local.chatonline.auth.model.http.response.JwtAuthenticationResponse;

import local.chatonline.auth.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@AllArgsConstructor
public class AuthEndpoint {

    UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody SignUpRequest signUpRequest) {

        log.info("Creating user: {}", signUpRequest.getUsername());

        User user = User
                .builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .userProfile(Profile
                        .builder()
                        .displayName(signUpRequest.getName())
                        .profilePictureUrl(signUpRequest.getProfilePicUrl()
                                ).build())
                .build();

        try {
            userService.registerUser(user, Role.USER);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));

    }
}