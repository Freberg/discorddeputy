package com.freberg.discorddeputy.command;

import java.util.function.Consumer;

import discord4j.core.object.entity.Message;

public interface Command extends Consumer<Message> {

    String getCommand();

    String getDescription();

    String getHelp();

    default void sendHelpReponse(Message message) {
        message.getChannel()
               .flatMap(channel -> channel.createMessage(getDetailedDescription()))
               .subscribe();
    }

    default String getDetailedDescription() {
        return getShortDescription()  + "\n" + getHelp();
    }

    default String getShortDescription() {
        return getCommand() + " - " + getDescription();
    }
}
