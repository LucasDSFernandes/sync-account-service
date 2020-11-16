package com.lucasdsf.syncaccountservice.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.config.PropertiesFiles;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
class AccountFormatTest {
	@MockBean
	PropertiesFiles properties;
	
	@Autowired
	AccountFormat accountFormat;
	
	@Test
	void testFormatAccount() {
		BDDMockito.given(properties.getAccountLength()).willReturn(6);
		assertThat(accountFormat.formatAccount("1").equals("000001"));
	}
	@Test
	void testFormatAgency() {
		BDDMockito.given(properties.getAgencyLength()).willReturn(4);
		assertThat(accountFormat.formatAgency("1").equals("0001"));
	}
	@Test
	void testFormatBalance() {
		assertThat(accountFormat.formatBalance("1000,00")==1000.00);
	}

}
