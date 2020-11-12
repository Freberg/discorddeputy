package com.freberg.discorddeputy.message;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$CriteriaMirrors;

@Value.Immutable
@$CriteriaMirrors.Criteria
@$CriteriaMirrors.CriteriaRepository
@JsonSerialize(as = ImmutableOffer.class)
@JsonDeserialize(as = ImmutableOffer.class)
public interface Offer extends Message {

    @Schema(description = "The unique identifier for the message",
            required = true)
    @$CriteriaMirrors.CriteriaId
    String getId();

    @Schema(description = "The offer title",
            required = true)
    String getTitle();

    @Schema(description = "A description about what the offering is",
            required = true)
    String getDescription();

    @Schema(description = "A URL pointing to where the offer can be found",
            required = true)
    String getUrl();

    @Schema(description = "The time when the offers starts to be in effect",
            required = true)
    Instant getStartTime();

    @Schema(description = "The time when the offers stops to be in effect",
            required = true)
    Instant getEndTime();

    @Schema(description = "A collection of various images relating to the offer",
            required = true)
    List<ImageUrl> getImageUrls();

    @Override
    default MessageType getMessageType() {
        return MessageType.OFFER;
    }
}
