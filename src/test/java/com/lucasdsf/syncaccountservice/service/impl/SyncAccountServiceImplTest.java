package com.lucasdsf.syncaccountservice.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.config.PropertiesFile;
import com.lucasdsf.syncaccountservice.services.account.impl.SyncAccountServiceImpl;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;
import com.lucasdsf.syncaccountservice.util.FileUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class SyncAccountServiceImplTest {

	@MockBean
	private FileUtils fileUtilMock;

	@MockBean(name = "csvFileService")
	private CsvServiceImpl csvServiceImpl;

	@MockBean
	private PropertiesFile propertiesMock;

	@Autowired
	private SyncAccountServiceImpl syncAccountServiceMock;

	@Test
	public void testeExecuteProcessFile() {
		String inputFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\test.csv");
		BDDMockito.given(fileUtilMock.putStrategyFile()).willReturn(fileUtilMock);
		BDDMockito.given(fileUtilMock.getStrategyFile(inputFilePath)).willReturn(csvServiceImpl);
		BDDMockito.given(fileUtilMock.getOutputFilePath(Mockito.anyString(), Mockito.anyString())).willReturn("");

		BDDMockito.given(csvServiceImpl.processFile(inputFilePath)).willReturn(true);

		syncAccountServiceMock.executeProcessFile(inputFilePath);
	}

}
