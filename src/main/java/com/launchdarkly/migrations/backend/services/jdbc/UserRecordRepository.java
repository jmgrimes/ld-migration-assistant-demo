package com.launchdarkly.migrations.backend.services.jdbc;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRecordRepository extends CrudRepository<UserRecord, UUID> {
}
