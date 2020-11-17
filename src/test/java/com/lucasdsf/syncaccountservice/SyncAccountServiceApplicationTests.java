package com.lucasdsf.syncaccountservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
class SyncAccountServiceApplicationTests {

	@Test
	void testMain() {
		assertThat(true);
	}

}
