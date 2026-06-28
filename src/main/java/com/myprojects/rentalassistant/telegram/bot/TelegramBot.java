package com.myprojects.rentalassistant.telegram.bot;

import com.myprojects.rentalassistant.config.TelegramProperties;
import com.myprojects.rentalassistant.telegram.handler.TelegramUpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot {

    private final TelegramProperties telegramProperties;
    private final TelegramUpdateHandler telegramUpdateHandler;

    @Override
    public String getBotToken() {
        return telegramProperties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return telegramUpdateHandler;
    }

}
