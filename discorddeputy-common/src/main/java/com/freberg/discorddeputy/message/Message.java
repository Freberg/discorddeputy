package com.freberg.discorddeputy.message;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

public interface Message {

    @Schema(description = "The source timestamp of the message",
            required = true)
    Instant getSourceTimestamp();

    @Schema(description = "The process timestamp of the message",
            required = true)
    Instant getProcessTimestamp();

    @Schema(description = "The source of the message",
            required = true)
    String getSource();

    @Schema(description = "The type of message",
            required = true)
    MessageType getMessageType();
}
