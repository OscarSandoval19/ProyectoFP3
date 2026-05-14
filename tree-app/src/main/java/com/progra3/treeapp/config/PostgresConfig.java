package com.progra3.treeapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(name = "app.storage", havingValue = "postgres")
@EnableJpaRepositories(basePackages = "com.progra3.treeapp.repository.postgres")
public class PostgresConfig {
}
