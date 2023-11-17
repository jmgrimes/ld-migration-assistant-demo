package com.launchdarkly.migrations.backend.services.jdbc;

import com.launchdarkly.migrations.backend.models.User;
import com.launchdarkly.migrations.backend.services.UserService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Component("jdbcUserService")
public class UserServiceImpl implements UserService {

    private UserRecordRepository userRecordRepository;

    public @NonNull Optional<? extends User> getUser(@NonNull final UUID id) {
        return getUserRecordRepository().findById(id);
    }

    public @NonNull Iterable<? extends User> getUsers() {
        return getUserRecordRepository().findAll();
    }

    public @NonNull Iterable<? extends User> getUsers(final Iterable<UUID> ids) {
        return getUserRecordRepository().findAllById(ids);
    }

    public @NonNull User saveUser(@NonNull final User user) {
        return getUserRecordRepository().save(new UserRecord(user));
    }

    public @NonNull Optional<? extends User> deleteUser(@NonNull final UUID id) {
        var user = getUserRecordRepository().findById(id);
        user.ifPresent(getUserRecordRepository()::delete);
        return user;
    }

}
