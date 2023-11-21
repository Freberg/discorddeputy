package com.freberg.discorddeputy.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommandFactory {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandFactory(ListOffersCommand listOffersCommand) {
        register(new HelpCommand(this));
        register(listOffersCommand);
    }

    private void register(Command command) {
        commands.compute(command.getCommand(), (key, value) -> {
            if (value != null) {
                log.error("Command for \"{}\" already registered, \"{}\"", key, value);
                throw new RuntimeException("Colliding discord commands detected");
            }
            return command;
        });
    }

    public Command resolveCommandFromString(String command) {
        return commands.get(command);
    }

    public Collection<Command> getAllCommands() {
        return commands.values();
    }
}
