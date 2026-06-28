package com.myprojects.rentalassistant.whatsapp.parser;

import java.time.LocalDateTime;

public record ParsedWhatsAppMessage(
        String sender,
        LocalDateTime messageTimestamp,
        String messageText
) {
}
