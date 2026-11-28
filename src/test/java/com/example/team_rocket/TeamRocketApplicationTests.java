package com.example.team_rocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:teamrocket-test;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password="
})
class TeamRocketApplicationTests {

	@Test
	void contextLoads() {
	}

}
