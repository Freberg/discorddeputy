package com.freberg.discorddeputy.repository;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Map;

@Configuration
public class JsonConverters {

    private final ObjectMapper objectMapper;

    public JsonConverters(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, Arrays.asList(
                new MapToJsonConverter(objectMapper),
                new JsonToMapConverter(objectMapper)
        ));
    }

    @WritingConverter
    static class MapToJsonConverter implements Converter<Map<String, Object>, Json> {
        private final ObjectMapper objectMapper;

        public MapToJsonConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Json convert(Map<String, Object> source) {
            try {
                return Json.of(objectMapper.writeValueAsString(source));
            } catch (JacksonException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ReadingConverter
    static class JsonToMapConverter implements Converter<Json, Map<String, Object>> {
        private final ObjectMapper objectMapper;

        public JsonToMapConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Map<String, Object> convert(Json source) {
            try {
                return objectMapper.readValue(source.asString(), new TypeReference<>() {
                });
            } catch (JacksonException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
