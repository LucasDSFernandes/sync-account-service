package com.lucasdsf.syncaccountservice.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
class FileUtilsTest {
	
	@Autowired
	private FileUtils fileUtil;
	
	@MockBean
	private CsvServiceImpl csvServiceImplMock;
	
	@Test
	void testCreateOutputFile() {
		String outFilePathFileMock = fileUtil.getOutputFilePath("/file-account", ".csv");
		assertThat(outFilePathFileMock.contains("file-account"));
	}
	
	@Test
	void testAppenndeWriterFile() {
		String outFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\testOut.csv");
		FileWriter fileWriter;
		Writer writer = null;
		try {
			fileWriter = new FileWriter(new File(outFilePath));
			writer = fileUtil.appendFile(fileWriter, "test");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertThat(writer !=null);
	}
	@Test
	void testPutStrategyFile() {
		String outFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\testOut.csv");

		fileUtil = fileUtil.putStrategyFile();
		assertThat(fileUtil.getStrategyFile(outFilePath) == csvServiceImplMock);
	}
	
	@Test
	void testGetResultFileInputStream() {
		String filePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\testOut.csv");
		FileInputStream fileInputStream  = fileUtil.getResultFileInputStream(filePath);
		fileUtil.closeFile(fileInputStream);
		assertThat(fileInputStream !=null);
	}

	@Test
	void testBuildFileLine() {
		String fileInputStream  = fileUtil.buildFileLine(buildAccountInfoDto(), Constants.CSV_SEPARATOR);
		assertThat(fileInputStream !=null);
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
