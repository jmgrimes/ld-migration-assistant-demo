package com.launchdarkly.migrations.backend;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "launchdarkly.sdk")
public class LaunchDarklySdkConfigurationProperties {

    private String clientSideId;
    private String mobileKey;
    private String serverSideKey;

}
