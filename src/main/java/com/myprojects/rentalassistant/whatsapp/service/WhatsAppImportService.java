package com.myprojects.rentalassistant.whatsapp.service;

import com.myprojects.rentalassistant.rawmessage.entity.RawMessage;
import com.myprojects.rentalassistant.rawmessage.repository.RawMessageRepository;
import com.myprojects.rentalassistant.whatsapp.parser.ParsedWhatsAppMessage;
import com.myprojects.rentalassistant.whatsapp.parser.WhatsAppChatParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WhatsAppImportService {

    private final WhatsAppChatParser parser;
    private final RawMessageRepository rawMessageRepository;

    @Transactional
    public WhatsAppImportResult importFile(Path chatFile) throws java.io.IOException {
        String chatText = Files.readString(chatFile, StandardCharsets.UTF_8);
        return importText(chatText);
    }

    @Transactional
    public WhatsAppImportResult importText(String chatText) {
        List<ParsedWhatsAppMessage> parsedMessages = parser.parse(chatText);
        int savedMessages = 0;
        int duplicateMessages = 0;

        for (ParsedWhatsAppMessage parsedMessage : parsedMessages) {
            String messageHash = hash(parsedMessage);
            if (rawMessageRepository.existsByMessageHash(messageHash)) {
                duplicateMessages++;
                continue;
            }

            rawMessageRepository.save(new RawMessage(
                    parsedMessage.sender(),
                    parsedMessage.messageTimestamp(),
                    parsedMessage.messageText(),
                    messageHash
            ));
            savedMessages++;
        }

        return new WhatsAppImportResult(parsedMessages.size(), savedMessages, duplicateMessages);
    }

    private String hash(ParsedWhatsAppMessage message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((
                    message.messageTimestamp()
                            + "|"
                            + message.sender()
                            + "|"
                            + message.messageText()
            ).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 digest is unavailable", ex);
        }
    }
}
