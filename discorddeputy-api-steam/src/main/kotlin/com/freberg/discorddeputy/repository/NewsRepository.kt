package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.message.News
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface NewsRepository: ReactiveMongoRepository<News, String>
