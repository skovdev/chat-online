package local.chatonline.auth.endpoint;

import local.chatonline.auth.exception.ResourceNotFoundException;

import local.chatonline.auth.model.User;
import local.chatonline.auth.model.CustomUser;
import local.chatonline.auth.model.UserSummary;

import local.chatonline.auth.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@AllArgsConstructor
public class UserEndpoint {

    UserService userService;

    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@PathVariable("username") String username) {

        log.info("Retrieving user: {}", username);

        return userService
                .findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(username));

    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        log.info("Retrieving all users");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(value = "/users/summaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllUserSummaries(@AuthenticationPrincipal CustomUser customUser) {
        log.info("Retrieving all users summaries");
        return ResponseEntity.ok(userService
                .findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(customUser.getUsername()))
                .map(this::convertTo));
    }

    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public UserSummary getCurrentUser(@AuthenticationPrincipal CustomUser customUser) {

        return UserSummary
                .builder()
                .id(customUser.getId())
                .username(customUser.getUsername())
                .name(customUser.getUserProfile().getDisplayName())
                .profilePicture(customUser.getUserProfile().getProfilePictureUrl())
                .build();

    }

    private UserSummary convertTo(User user) {

        return UserSummary
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getDisplayName())
                .profilePicture(user.getUserProfile().getProfilePictureUrl())
                .build();

    }
}