package com.freberg.discorddeputy.command;

import java.util.function.Consumer;

import discord4j.core.object.entity.Message;

public interface Command extends Consumer<Message> {

    String getCommand();
}
