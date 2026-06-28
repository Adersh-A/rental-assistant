package com.myprojects.rentalassistant.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    private final TelegramProperties properties;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(properties.token());
    }
}