package com.freberg.discorddeputy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.lang.NonNull;

@Configuration
public class R2dbcConfig {

    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @NonNull
            @Override
            public String getColumnName(@NonNull RelationalPersistentProperty property) {
                return property.getName();
            }
        };
    }
}
