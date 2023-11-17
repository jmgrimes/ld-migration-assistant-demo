package com.launchdarkly.migrations.backend.support;

import com.launchdarkly.sdk.ContextKind;
import com.launchdarkly.sdk.LDContext;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import ua_parser.Parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class LDContextProviderImpl implements LDContextProvider {

    private HttpServletRequest request;

    public @NonNull LDContext getContext() {
        return LDContext.multiBuilder()
                .add(getUserContext())
                .add(getDeviceContext())
                .build();
    }

    private @NonNull LDContext getUserContext() {
        // TODO Add custom configuration for the user context here.

        // Likely, this will leverage the HttpServletRequest object and some additional database
        // logic to retrieve information about your user to be returned.  Be sure that you do not include any
        // sensitive information in this context.
        var userKey = UUID.randomUUID().toString();
        return LDContext.builder(ContextKind.of("user"), userKey)
                .anonymous(true)
                .build();
    }

    private @NonNull LDContext getDeviceContext() {
        var userAgent = getRequest().getHeader(HttpHeaders.USER_AGENT);
        if (Objects.nonNull(userAgent)) {
            var userAgentParser = new Parser();
            var clientInfo = userAgentParser.parse(userAgent);
            var osVersion = String.format("%s.%s.%s", clientInfo.os.major, clientInfo.os.minor, clientInfo.os.patch);
            var uaVersion = String.format(
                    "%s.%s.%s", clientInfo.userAgent.major, clientInfo.userAgent.minor, clientInfo.userAgent.patch);
            return LDContext.builder(ContextKind.of("device"), userAgent)
                    .set("family", clientInfo.device.family)
                    .set("osFamily", clientInfo.os.family)
                    .set("osVersion", osVersion)
                    .set("uaFamily", clientInfo.userAgent.family)
                    .set("uaVersion", uaVersion)
                    .build();
        }
        var deviceKey = UUID.randomUUID().toString();
        return LDContext.builder(ContextKind.of("device"), deviceKey)
                .anonymous(true)
                .build();
    }

}
