package com.freberg.discorddeputy.command;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import discord4j.core.object.entity.Message;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final CommandFactory commandFactory;

    @Override
    public String getCommand() {
        return "!help";
    }

    @Override
    public String getDescription() {
        return "prints helpful information about existing commands or a specific command";
    }

    @Override
    public String getHelp() {
        return "[COMMAND]\n";
    }

    @Override
    public void accept(Message message) {
        message.getChannel()
                .flatMap(channel -> channel.createMessage(getHelpResponse(message.getContent())))
                .subscribe();
    }

    private String getHelpResponse(String content) {
        return Optional.ofNullable(content)
                .map(str -> str.split(" "))
                .filter(words -> words.length == 2)
                .map(words -> commandFactory.resolveCommandFromString(words[1]))
                .map(Command::getDetailedDescription)
                .orElse(commandFactory.getAllCommands().stream()
                        .sorted(Comparator.comparing(Command::getCommand))
                        .map(Command::getShortDescription)
                        .collect(Collectors.joining("\n")));
    }
}
