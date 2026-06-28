package com.myprojects.rentalassistant.whatsapp.service;

public record WhatsAppImportResult(
        int parsedMessages,
        int savedMessages,
        int duplicateMessages
) {
}
