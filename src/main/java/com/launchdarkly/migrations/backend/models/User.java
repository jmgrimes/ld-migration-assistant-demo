package com.launchdarkly.migrations.backend.models;

import java.util.UUID;

public interface User {

    UUID id();

    String firstName();

    String lastName();

    String title();

    String email();

    String phone();

}
