package com.launchdarkly.migrations.backend.controllers;

import com.launchdarkly.migrations.backend.support.LDConfigurationProvider;

import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import com.launchdarkly.shaded.com.google.gson.Gson;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@AllArgsConstructor
@Getter
@RestController
@RequestMapping("/api/v1/launchdarkly/Status")
public class StatusController {

    private static final Gson GSON = new Gson();

    private LDConfigurationProvider configurationProvider;
    private LDClientInterface ldClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ByteArrayResource> getStatus() {
        var statusResource = new StatusResource(
                getLdClient().isInitialized(),
                Objects.nonNull(getConfigurationProvider().getClientSideId()),
                Objects.nonNull(getConfigurationProvider().getMobileKey())
        );
        var responseBody = GSON.toJson(statusResource).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok(new ByteArrayResource(responseBody));
    }

    // Because the LaunchDarkly Java Server SDK uses a shaded Gson implementation under the hood, we rely on that
    // to serialize the status resource object appropriately, rather than use the default Jackson ObjectMapper approach.
    // For this reason, we've opted to keep the model class and serialization within this controller.

    @AllArgsConstructor
    public static class StatusResource {

        @Getter private boolean initialized;
        @Getter private boolean clientSdkSupported;
        @Getter private boolean mobileSdkSupported;

    }

}
