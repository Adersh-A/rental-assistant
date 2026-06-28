package com.myprojects.rentalassistant.telegram.service;

import com.myprojects.rentalassistant.whatsapp.service.WhatsAppImportResult;
import com.myprojects.rentalassistant.whatsapp.service.WhatsAppImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramFileService {

    private final TelegramClient telegramClient;
    private final WhatsAppImportService whatsAppImportService;

    public void process(Document document){
        GetFile getFile = GetFile.builder()
                .fileId(document.getFileId())
                .build();

        try {
            File telegramFile = telegramClient.execute(getFile);
            java.io.File downloadedFile = telegramClient.downloadFile(telegramFile);
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            Path destination = uploadDir.resolve(
                    document.getFileName()
            );
            Files.copy(
                    downloadedFile.toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
            log.info("File saved to {}", destination);
            WhatsAppImportResult importResult = whatsAppImportService.importFile(destination);
            log.info(
                    "WhatsApp import completed for {}: parsed={}, saved={}, duplicates={}",
                    destination,
                    importResult.parsedMessages(),
                    importResult.savedMessages(),
                    importResult.duplicateMessages()
            );
        } catch (TelegramApiException | IOException e) {
            log.error("Failed to process file {}", document.getFileName(), e);
        }

    }
}
