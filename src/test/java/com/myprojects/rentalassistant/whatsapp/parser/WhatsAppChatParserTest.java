package com.myprojects.rentalassistant.whatsapp.parser;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WhatsAppChatParserTest {

    private final WhatsAppChatParser parser = new WhatsAppChatParser();

    @Test
    void parsesUserMessagesAndMultilineContent() {
        String chatText = """
                12/12/25, 8:32\u202Fpm - Messages and calls are end-to-end encrypted. Only people in this chat can read, listen to, or share them. *Learn more*
                12/12/25, 8:53\u202Fpm - Rental properties kakkanad: 2BHK FULLY FURNISHED FLAT FOR RENT - KAKKANAD INFOPARK
                FOR FAMILY ONLY
                RENT 32,000 INCLUDING MAINTENANCE

                CONTACT: 095391 10237
                13/12/25, 11:03\u202Fam - +91 94478 93825: NEAR INFOPARK
                3 BHK FULLY FURNISHED FLAT
                RENT 33 K ONLY
                DM 9447893825
                13/12/25, 3:06\u202Fpm - +91 96451 13997 was added
                """;

        List<ParsedWhatsAppMessage> messages = parser.parse(chatText);

        assertThat(messages).hasSize(2);
        assertThat(messages.getFirst().sender()).isEqualTo("Rental properties kakkanad");
        assertThat(messages.getFirst().messageTimestamp()).isEqualTo(LocalDateTime.of(2025, 12, 12, 20, 53));
        assertThat(messages.getFirst().messageText()).contains("2BHK FULLY FURNISHED");
        assertThat(messages.getFirst().messageText()).contains("CONTACT: 095391 10237");
        assertThat(messages.getLast().sender()).isEqualTo("+91 94478 93825");
        assertThat(messages.getLast().messageTimestamp()).isEqualTo(LocalDateTime.of(2025, 12, 13, 11, 3));
        assertThat(messages.getLast().messageText()).contains("RENT 33 K ONLY");
    }

    @Test
    void parsesFourDigitYearAndTwentyFourHourTime() {
        String chatText = "21/06/2026, 15:21 - Broker: 1 BHK Rent 20000";

        List<ParsedWhatsAppMessage> messages = parser.parse(chatText);

        assertThat(messages).singleElement()
                .satisfies(message -> {
                    assertThat(message.sender()).isEqualTo("Broker");
                    assertThat(message.messageTimestamp()).isEqualTo(LocalDateTime.of(2026, 6, 21, 15, 21));
                    assertThat(message.messageText()).isEqualTo("1 BHK Rent 20000");
                });
    }

    @Test
    void ignoresUtf8ByteOrderMarkAtStartOfExport() {
        String chatText = "\uFEFF21/06/26, 3:21 pm - Broker: 1 BHK Rent 20000";

        List<ParsedWhatsAppMessage> messages = parser.parse(chatText);

        assertThat(messages).singleElement()
                .satisfies(message -> assertThat(message.sender()).isEqualTo("Broker"));
    }
}
