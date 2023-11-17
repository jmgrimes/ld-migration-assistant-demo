package com.launchdarkly.migrations.backend.controllers;

import com.launchdarkly.migrations.backend.models.User;
import com.launchdarkly.migrations.backend.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Getter
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResource>> getUsers() {
        var users = userService.getUsers();
        return ResponseEntity.ok(StreamSupport.stream(users.spliterator(), false)
                .map(UserResource::new)
                .collect(Collectors.toList()
        ));
    }

    @PostMapping
    public ResponseEntity<UserResource> newUser(@RequestBody final UserBody userBody) {
        var user = new UserImpl(userBody);
        var newUser = getUserService().saveUser(user);
        return ResponseEntity.ok(new UserResource(newUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResource> getUser(@PathVariable("id") final UUID id) {
        var user = userService.getUser(id);
        return user
                .map(UserResource::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResource> updateUser(@PathVariable("id") final UUID id, @RequestBody final UserBody userBody) {
        var user = new UserImpl(id, userBody);
        var updatedUser = getUserService().saveUser(user);
        return ResponseEntity.ok(new UserResource(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResource> deleteUser(@PathVariable("id") final UUID id) {
        var user = getUserService().deleteUser(id);
        return user
                .map(UserResource::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @AllArgsConstructor
    @Getter
    public static class UserResource {
        private UUID id;
        private String firstName;
        private String lastName;
        private String title;
        private String email;
        private String phone;

        public UserResource(final User user) {
            this(user.id(), user.firstName(), user.lastName(), user.title(), user.email(), user.phone());
        }
    }

    public record UserBody(String firstName, String lastName, String title, String email, String phone) {}

    public record UserImpl(
            UUID id, String firstName, String lastName, String title, String email, String phone) implements User {

        public UserImpl(final UserBody userBody) {
            this(UUID.randomUUID(), userBody.firstName(), userBody.lastName(), userBody.title(),
                    userBody.email(), userBody.phone());
        }

        public UserImpl(final UUID id, final UserBody userBody) {
            this(id, userBody.firstName(), userBody.lastName(), userBody.title(), userBody.email(), userBody.phone());
        }

    }

}
