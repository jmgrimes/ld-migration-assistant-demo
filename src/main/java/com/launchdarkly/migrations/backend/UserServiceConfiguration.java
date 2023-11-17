package com.launchdarkly.migrations.backend;

import com.launchdarkly.migrations.backend.support.LDContextProvider;
import com.launchdarkly.migrations.backend.services.UserService;
import com.launchdarkly.migrations.backend.services.migration.UserServiceImpl;

import com.launchdarkly.sdk.server.MigrationStage;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfiguration {

    @Bean
    public UserService userService(
            final LDClientInterface ldClient,
            final LDContextProvider ldContextProvider,
            @Qualifier("jdbcUserService") final UserService oldService,
            @Qualifier("mongodbUserService") final UserService newService) {
        return new UserServiceImpl(
                ldClient,
                ldContextProvider,
                oldService,
                newService,
                "user-service-migration",
                MigrationStage.OFF
        );
    }

}
