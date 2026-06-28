package com.myprojects.rentalassistant.whatsapp.service;

import com.myprojects.rentalassistant.rawmessage.repository.RawMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "telegrambots.enabled=false",
        "telegram.token=test-token",
        "spring.datasource.url=jdbc:h2:mem:whatsappimport;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class WhatsAppImportServiceTest {

    @Autowired
    private WhatsAppImportService importService;

    @Autowired
    private RawMessageRepository rawMessageRepository;

    @Test
    void storesParsedMessagesAndSkipsDuplicatesOnReimport() {
        String chatText = """
                21/06/26, 3:21 pm - Broker: 1 BHK Rent 20000
                21/06/26, 3:22 pm - Broker: 2 BHK Rent 30000
                """;

        WhatsAppImportResult firstImport = importService.importText(chatText);
        WhatsAppImportResult secondImport = importService.importText(chatText);

        assertThat(firstImport.parsedMessages()).isEqualTo(2);
        assertThat(firstImport.savedMessages()).isEqualTo(2);
        assertThat(firstImport.duplicateMessages()).isZero();
        assertThat(secondImport.parsedMessages()).isEqualTo(2);
        assertThat(secondImport.savedMessages()).isZero();
        assertThat(secondImport.duplicateMessages()).isEqualTo(2);
        assertThat(rawMessageRepository.count()).isEqualTo(2);
    }
}
