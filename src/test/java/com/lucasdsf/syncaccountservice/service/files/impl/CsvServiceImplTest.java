package com.lucasdsf.syncaccountservice.service.files.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.config.PropertiesFile;
import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountFileEnum;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;
import com.lucasdsf.syncaccountservice.util.AccountFormat;
import com.lucasdsf.syncaccountservice.util.FileUtils;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
class CsvServiceImplTest {
	@MockBean
	private FileUtils fileUtilMock;
	
	@Autowired
	private CsvServiceImpl csvServiceImpl;
	
	@MockBean
	private AccountFormat accountFormat;
	
	@MockBean
	private PropertiesFile propertiesMock;
	
	@Test
	void testProcessFile() {
		String inputFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\test.csv");
		String outFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\testOut.csv");
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream( new File(inputFilePath));
			
			BDDMockito.given(propertiesMock.getOutputFileName()).willReturn("testOut");
			BDDMockito.given(propertiesMock.getOutputFileExtension()).willReturn("csv");
			
			BDDMockito.given(fileUtilMock.getOutputFilePath("testOut", "csv")).willReturn(outFilePath);
			
			FileWriter fileWriter = new FileWriter(new File(outFilePath));
			BDDMockito.given(fileUtilMock.appendFile(fileWriter, getCsvHeader())).willReturn(fileWriter);
			BDDMockito.given(accountFormat.formatAgency( this.buildAccountInfoDto().getAgencia() )).willReturn( this.buildAccountInfoDto().getAgencia() );
			BDDMockito.given(accountFormat.formatAccount( this.buildAccountInfoDto().getConta() )).willReturn( this.buildAccountInfoDto().getConta() );
			BDDMockito.given(accountFormat.formatBalance(String.valueOf(this.buildAccountInfoDto().getSaldo()))).willReturn(this.buildAccountInfoDto().getSaldo());
			
			csvServiceImpl.processFile(fileInputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void testGetResultFileRead() {
		String inputFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\test.csv");
		FileInputStream fileInputStream= csvServiceImpl.getResultFileInputStream(inputFilePath);
		assertThat(Objects.nonNull(fileInputStream));
	}
	
	
	private String getCsvHeader() {
		return AccountFileEnum.getHeadersName().stream().collect(Collectors.joining(Constants.CSV_SEPARATOR)).concat(Constants.NEW_LINE_FUNCTION);
	}
	
	private AccountInfoDTO buildAccountInfoDto() {
		AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
		accountInfoDTO.setAgencia("1234");
		accountInfoDTO.setConta("123456");
		accountInfoDTO.setSaldo(20.2);
		accountInfoDTO.setStatus(AccountStatusEnum.A);
		return accountInfoDTO;
	}

}
