package com.launchdarkly.migrations.backend.support;

import com.launchdarkly.sdk.server.LDConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LDConfigurationProviderImpl implements LDConfigurationProvider {

    private String clientSideId;
    private String mobileKey;
    private String serverSideKey;
    private LDConfig configuration;

}
