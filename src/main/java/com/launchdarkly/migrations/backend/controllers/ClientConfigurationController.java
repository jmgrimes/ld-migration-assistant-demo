package com.launchdarkly.migrations.backend.controllers;

import com.launchdarkly.migrations.backend.support.LDConfigurationProvider;
import com.launchdarkly.migrations.backend.support.LDContextProvider;

import com.launchdarkly.sdk.LDContext;
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
@RequestMapping("/api/v1/launchdarkly/ClientConfiguration")
public class ClientConfigurationController {

    private static final Gson GSON = new Gson();

    private LDConfigurationProvider configurationProvider;
    private LDContextProvider contextProvider;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ByteArrayResource> getClientConfiguration() {
        if (Objects.nonNull(getConfigurationProvider().getClientSideId())) {
            var clientConfiguration = new ClientConfigurationResource(
                    getConfigurationProvider().getClientSideId(),
                    getContextProvider().getContext()
            );
            var responseBody = GSON.toJson(clientConfiguration).getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok(new ByteArrayResource(responseBody));
        }
        return ResponseEntity.notFound().build();
    }

    // Because the LaunchDarkly Java Server SDK uses a shaded Gson implementation under the hood, we rely on that
    // to serialize the configuration object appropriately, rather than use the default Jackson ObjectMapper approach.
    // For this reason, we've opted to keep the model class and serialization within this controller.

    @AllArgsConstructor
    public static class ClientConfigurationResource {

        @Getter private String clientSideId;
        @Getter private LDContext context;

    }

}
