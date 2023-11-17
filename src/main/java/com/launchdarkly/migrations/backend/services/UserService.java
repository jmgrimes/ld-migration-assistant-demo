package com.launchdarkly.migrations.backend.services;

import com.launchdarkly.migrations.backend.models.User;

import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    @NonNull Optional<? extends User> getUser(@NonNull final UUID id);

    @NonNull Iterable<? extends User> getUsers();

    @NonNull Iterable<? extends User> getUsers(@NonNull final Iterable<UUID> ids);

    @NonNull User saveUser(@NonNull final User user);

    @NonNull Optional<? extends User> deleteUser(@NonNull final UUID id);

}
