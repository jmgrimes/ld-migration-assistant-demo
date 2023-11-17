package com.launchdarkly.migrations.backend;

import com.launchdarkly.migrations.backend.support.LDConfigurationProvider;
import com.launchdarkly.migrations.backend.support.LDConfigurationProviderImpl;
import com.launchdarkly.migrations.backend.support.LDContextProvider;
import com.launchdarkly.migrations.backend.support.LDContextProviderImpl;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Getter
@Configuration
@EnableConfigurationProperties(value = LaunchDarklySdkConfigurationProperties.class)
public class LaunchDarklySdkConfiguration {

    private LaunchDarklySdkConfigurationProperties configurationProperties;

    @Bean
    @RequestScope
    public LDContextProvider contextProvider(final HttpServletRequest request) {
        return new LDContextProviderImpl(request);
    }

    @Bean
    public LDConfigurationProvider configurationProvider() {
        // TODO Add any custom configuration you need for the LaunchDarkly client's server configuration here.
        var configuration = new LDConfig.Builder().build();
        return new LDConfigurationProviderImpl(
                configurationProperties.getClientSideId(),
                configurationProperties.getMobileKey(),
                configurationProperties.getServerSideKey(),
                configuration
        );
    }

    @Bean(destroyMethod = "close")
    public LDClientInterface client(final LDConfigurationProvider configurationProvider) {
        return new LDClient(
                configurationProvider.getServerSideKey(),
                configurationProvider.getConfiguration()
        );
    }

}
