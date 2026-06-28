package com.myprojects.rentalassistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"telegrambots.enabled=false",
		"telegram.token=test-token",
		"spring.datasource.url=jdbc:h2:mem:rentalassistant;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=validate"
})
class RentalAssistantApplicationTests {

	@Test
	void contextLoads() {
	}

}
