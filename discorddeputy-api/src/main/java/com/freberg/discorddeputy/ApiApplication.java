package com.freberg.discorddeputy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

@SpringBootApplication
@SuppressWarnings("unused")
public class ApiApplication {

    @Bean
	  JdbcPostgresDialect dialect() {
		  return JdbcPostgresDialect.INSTANCE;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }
}
