package com.launchdarkly.migrations.backend.controllers;

import com.launchdarkly.migrations.backend.support.LDConfigurationProvider;
import com.launchdarkly.migrations.backend.support.LDContextProvider;

import com.launchdarkly.sdk.server.FlagsStateOption;
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
@RequestMapping("/api/v1/launchdarkly/ClientBootstrap")
public class ClientBootstrapController {

    private static final Gson GSON = new Gson();

    private LDClientInterface ldClient;
    private LDConfigurationProvider configurationProvider;
    private LDContextProvider contextProvider;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ByteArrayResource> getClientConfiguration() {
        if (Objects.nonNull(getConfigurationProvider().getClientSideId())) {
            var context = getContextProvider().getContext();
            var allFlagsState = getLdClient().allFlagsState(context, FlagsStateOption.CLIENT_SIDE_ONLY);
            var responseBody = GSON.toJson(allFlagsState).getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok(new ByteArrayResource(responseBody));
        }
        return ResponseEntity.notFound().build();
    }

}
