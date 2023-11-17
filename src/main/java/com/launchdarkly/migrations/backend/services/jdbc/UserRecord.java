package com.launchdarkly.migrations.backend.services.jdbc;

import com.launchdarkly.migrations.backend.models.User;

import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.util.UUID;

public record UserRecord(
        @Id UUID id, String firstName, String lastName, String title, String email, String phone) implements User {

    public UserRecord(@NonNull final User user) {
        this(user.id(), user.firstName(), user.lastName(), user.title(), user.email(), user.phone());
    }

}
