package com.launchdarkly.migrations.backend.support;

import com.launchdarkly.sdk.server.LDConfig;

public interface LDConfigurationProvider {

    String getClientSideId();

    String getMobileKey();

    String getServerSideKey();

    LDConfig getConfiguration();

}
