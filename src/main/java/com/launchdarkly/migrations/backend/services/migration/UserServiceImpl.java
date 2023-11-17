package com.launchdarkly.migrations.backend.services.migration;

import com.launchdarkly.migrations.backend.models.User;
import com.launchdarkly.migrations.backend.support.LDContextProvider;
import com.launchdarkly.migrations.backend.services.UserService;

import com.launchdarkly.sdk.server.MigrationStage;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import com.launchdarkly.sdk.server.migrations.Migration;
import com.launchdarkly.sdk.server.migrations.MigrationBuilder;
import com.launchdarkly.sdk.server.migrations.MigrationExecution;
import org.springframework.lang.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    private String flagKey;
    private MigrationStage defaultStage;
    private LDContextProvider ldContextProvider;
    private Migration<
            UserServiceReader.Result,
            UserServiceWriter.Result,
            UserServiceReader.Operation,
            UserServiceWriter.Operation> migration;

    public UserServiceImpl(
            final LDClientInterface ldClient,
            final LDContextProvider ldContextProvider,
            final UserService oldService,
            final UserService newService,
            final String flagKey,
            final MigrationStage defaultStage) {
        super();
        setFlagKey(flagKey);
        setDefaultStage(defaultStage);
        setLdContextProvider(ldContextProvider);
        setMigration(new MigrationBuilder<
                UserServiceReader.Result,
                UserServiceWriter.Result,
                UserServiceReader.Operation,
                UserServiceWriter.Operation>(ldClient)
                .read(new UserServiceReader(oldService), new UserServiceReader(newService))
                .write(new UserServiceWriter(oldService), new UserServiceWriter(newService))
                .readExecution(MigrationExecution.Parallel())
                .trackLatency(true)
                .trackErrors(true)
                .build()
                .orElseThrow()
        );
    }

    @Override
    public @NonNull Optional<? extends User> getUser(@NonNull final UUID id) {
        var context = getLdContextProvider().getContext();
        var result = getMigration().read(getFlagKey(), context, getDefaultStage(), new UserServiceReader.GetUserOperation(id));
        return extractResult(result, r -> {
            var getUser = (UserServiceReader.GetUserResult) r;
            return getUser.user();
        });
    }

    @Override
    public @NonNull Iterable<? extends User> getUsers() {
        var context = getLdContextProvider().getContext();
        var result = getMigration().read(getFlagKey(), context, getDefaultStage(), new UserServiceReader.GetUsersOperation(Collections.emptySet()));
        return extractResult(result, r -> {
            var getUsers = (UserServiceReader.GetUsersResult) r;
            return getUsers.users();
        });
    }

    @Override
    public @NonNull Iterable<? extends User> getUsers(@NonNull final Iterable<UUID> ids) {
        var context = getLdContextProvider().getContext();
        var result = getMigration().read(getFlagKey(), context, getDefaultStage(), new UserServiceReader.GetUsersOperation(ids));
        return extractResult(result, r -> {
            var getUsers = (UserServiceReader.GetUsersResult) r;
            return getUsers.users();
        });
    }

    public @NonNull User saveUser(@NonNull final User user) {
        var context = getLdContextProvider().getContext();
        var result = getMigration().write(getFlagKey(), context, getDefaultStage(), new UserServiceWriter.SaveUserOperation(user));
        return extractResult(result.getAuthoritative(), r -> {
            var saveUser = (UserServiceWriter.SaveUserResult) r;
            return saveUser.user();
        });
    }

    public @NonNull Optional<? extends User> deleteUser(@NonNull final UUID id) {
        var context = getLdContextProvider().getContext();
        var result = getMigration().write(getFlagKey(), context, getDefaultStage(), new UserServiceWriter.DeleteUserOperation(id));
        return extractResult(result.getAuthoritative(), r -> {
            var deleteUser = (UserServiceWriter.DeleteUserResult) r;
            return deleteUser.user();
        });
    }

    private static <R, T> T extractResult(final Migration.MigrationResult<R> result, Function<R, T> extractor) {
        if (result.isSuccess() && result.getResult().isPresent()) {
            var operationResult = result.getResult().get();
            return extractor.apply(operationResult);
        }
        if (result.getException().isPresent()) {
            var exception = result.getException().get();
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            else {
                throw new UndeclaredThrowableException(exception);
            }
        }
        throw new IllegalStateException("migration method did not return a result, nor throw an exception");
    }

}
