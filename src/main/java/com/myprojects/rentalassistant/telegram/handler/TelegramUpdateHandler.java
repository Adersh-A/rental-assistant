package com.myprojects.rentalassistant.telegram.handler;

import com.myprojects.rentalassistant.telegram.service.TelegramFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramFileService telegramFileService;

    @Override
    public void consume(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasDocument()) return;

        Document document = update.getMessage().getDocument();

        String fileName = document.getFileName();

        if (fileName == null || !fileName.toLowerCase().endsWith(".txt")) return;

        telegramFileService.process(document);
    }
}
