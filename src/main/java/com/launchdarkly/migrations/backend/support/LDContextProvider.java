package com.launchdarkly.migrations.backend.support;

import com.launchdarkly.sdk.LDContext;
import org.springframework.lang.NonNull;

public interface LDContextProvider {

    @NonNull LDContext getContext();

}
