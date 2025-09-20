package com.freberg.discorddeputy;

import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Configuration
public class MongoClientConfiguration {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new DiscordNotificationWriteConverter(),
                new DiscordNotificationReadConverter()
        ));
    }

    static class DiscordNotificationWriteConverter implements Converter<DiscordNotification, Document> {

        @Override
        public Document convert(DiscordNotification source) {
            var document = new Document();
            document.put("_id", source.id());
            document.put("source", source.source());

            source.toMap().forEach((key, value) -> {
                if (value instanceof String) {
                    document.put(key, value);
                }
                if (!"id".equals(key) && !"source".equals(key)) {
                    document.put(key, value);
                }
            });
            return document;
        }
    }

    static class DiscordNotificationReadConverter  implements Converter<Document, DiscordNotification> {

        @Override
        public DiscordNotification convert(Document source) {
            var map = source.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            map.put("id", map.remove("_id"));
            return new DiscordNotification(map);
        }
    }
}
