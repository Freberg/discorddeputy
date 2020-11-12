package com.freberg.discorddeputy.controller

import com.freberg.discorddeputy.message.News
import com.freberg.discorddeputy.repository.NewsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/stream")
class SteamNewsController(val repository: NewsRepository) {

    @GetMapping("/latestNews")
    fun getLatest(): Flux<News> {
        return repository.findAll()
    }
}