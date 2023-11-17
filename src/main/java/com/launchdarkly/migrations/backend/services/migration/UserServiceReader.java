package com.launchdarkly.migrations.backend.services.migration;

import com.launchdarkly.migrations.backend.models.User;
import com.launchdarkly.migrations.backend.services.UserService;

import com.launchdarkly.sdk.server.migrations.Migration;
import com.launchdarkly.sdk.server.migrations.MigrationMethodResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Getter
public class UserServiceReader implements Migration.Reader<UserServiceReader.Operation, UserServiceReader.Result> {

    private UserService userService;

    public MigrationMethodResult<Result> execute(final Operation operation) {
        try {
            return switch (operation) {
                case GetUserOperation getUser -> MigrationMethodResult.Success(
                        new GetUserResult(getUserService().getUser(getUser.id()))
                );
                case GetUsersOperation getUsers -> {
                    final Set<UUID> ids = StreamSupport.stream(getUsers.ids().spliterator(), false).collect(Collectors.toSet());
                    yield MigrationMethodResult.Success(
                            ids.isEmpty() ?
                            new GetUsersResult(getUserService().getUsers()) :
                            new GetUsersResult(getUserService().getUsers(ids))
                    );
                }
            };
        }
        catch (final Exception exception) {
            return MigrationMethodResult.Failure(exception);
        }
    }

    public sealed interface Operation {}
    public record GetUserOperation(UUID id) implements Operation {}
    public record GetUsersOperation(Iterable<UUID> ids) implements Operation {}

    public sealed interface Result {}
    public record GetUserResult(Optional<? extends User> user) implements Result {}
    public record GetUsersResult(Iterable<? extends User> users) implements Result {}

}
