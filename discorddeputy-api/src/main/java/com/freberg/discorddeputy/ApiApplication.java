package com.freberg.discorddeputy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

@SpringBootApplication
@SuppressWarnings("unused")
public class ApiApplication {

    @Bean
    JdbcPostgresDialect dialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

    @Bean
    NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                return property.getName();
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }
}
