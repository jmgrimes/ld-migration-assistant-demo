package com.launchdarkly.migrations.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "com.launchdarkly.migrations.backend.services.jdbc")
@EnableMongoRepositories(basePackages = "com.launchdarkly.migrations.backend.services.mongodb")
public class MigrationsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MigrationsBackendApplication.class, args);
	}

}
