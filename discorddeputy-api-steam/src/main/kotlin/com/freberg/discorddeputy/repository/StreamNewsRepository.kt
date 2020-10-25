package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.message.steam.SteamNews
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface StreamNewsRepository: ReactiveMongoRepository<SteamNews, String>
