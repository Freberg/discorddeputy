package com.freberg.discorddeputy.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandFactoryTest {

    @Test
    void verifyNoCommandCollision() {
        CommandFactory commandFactory = new CommandFactory();
        commandFactory.init();
        Assertions.assertEquals(2, commandFactory.getAllCommands().size());
    }
}