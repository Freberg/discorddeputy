package com.freberg.discorddeputy.message;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$CriteriaMirrors;

@Value.Immutable
@$CriteriaMirrors.Criteria
@$CriteriaMirrors.CriteriaRepository
@JsonSerialize(as = ImmutableNews.class)
@JsonDeserialize(as = ImmutableNews.class)
public interface News extends Message {

    @Schema(description = "The unique identifier for the message",
            required = true)
    @$CriteriaMirrors.CriteriaId
    String getId();

    @Schema(description = "The news title",
            required = true)
    String getTitle();

    @Schema(description = "The news content",
            required = true)
    String getContent();

    @Schema(description = "A URL pointing to where the news can be found",
            required = true)
    String getUrl();

    @Schema(description = "A collection of various images relating to the news",
            required = true)
    List<ImageUrl> getImageUrls();

    @Override
    default MessageType getMessageType() {
        return MessageType.NEWS;
    }
}
