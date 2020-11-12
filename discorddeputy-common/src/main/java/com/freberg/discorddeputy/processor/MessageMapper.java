package com.freberg.discorddeputy.processor;

public interface MessageMapper<I, O> {

    O mapMessage(I inputMessage);
}
