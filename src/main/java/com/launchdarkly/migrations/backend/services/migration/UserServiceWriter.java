package com.launchdarkly.migrations.backend.services.migration;

import com.launchdarkly.migrations.backend.models.User;
import com.launchdarkly.migrations.backend.services.UserService;

import com.launchdarkly.sdk.server.migrations.Migration;
import com.launchdarkly.sdk.server.migrations.MigrationMethodResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserServiceWriter implements Migration.Writer<UserServiceWriter.Operation, UserServiceWriter.Result> {

    private UserService userService;

    public MigrationMethodResult<Result> execute(final Operation operation) {
        try {
            return switch (operation) {
                case SaveUserOperation saveUser -> MigrationMethodResult.Success(
                        new SaveUserResult(getUserService().saveUser(saveUser.user()))
                );
                case DeleteUserOperation deleteUser -> MigrationMethodResult.Success(
                        new DeleteUserResult(getUserService().deleteUser(deleteUser.id()))
                );
            };
        }
        catch (final Exception exception) {
            return MigrationMethodResult.Failure(exception);
        }
    }

    public sealed interface Operation {}
    public record SaveUserOperation(User user) implements Operation {}
    public record DeleteUserOperation(UUID id) implements Operation {}

    public sealed interface Result {}
    public record SaveUserResult(User user) implements Result {}
    public record DeleteUserResult(Optional<? extends User> user) implements Result {}

}
