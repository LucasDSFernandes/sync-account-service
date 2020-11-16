package com.lucasdsf.syncaccountservice.service.impl;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.config.PropertiesFiles;
import com.lucasdsf.syncaccountservice.services.account.impl.SyncAccountServiceImpl;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;
import com.lucasdsf.syncaccountservice.util.FileUtils;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class SyncAccountServiceImplTest {

	@MockBean
	private FileUtils fileUtilMock;
	
	@MockBean(name="csvFileService")
	private CsvServiceImpl csvServiceImpl;
	
	@MockBean
	private PropertiesFiles propertiesMock;
	
	@Autowired
	private SyncAccountServiceImpl syncAccountServiceMock;
	
	@Test
	public void testeExecuteProcessFile() {
		String inputFilePath = System.getProperty("user.dir").concat("\\src\\test\\resources\\test.csv");
		BDDMockito.given(fileUtilMock.putStrategyFile()).willReturn(fileUtilMock);
		BDDMockito.given(fileUtilMock.getFile(inputFilePath)).willReturn(csvServiceImpl);
		BDDMockito.given(fileUtilMock.createOutputFile(Mockito.anyString(), Mockito.anyString())).willReturn("");

//		String outputFileName = propertiesMock.getOutputFileName();
//		BDDMockito.given(outputFileName).willReturn("test_out");
//		String outputFileExtension = propertiesMock.getOutputFileExtension();
//		BDDMockito.given(outputFileExtension).willReturn("csv");
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream( new File(inputFilePath));
			BDDMockito.given(csvServiceImpl.getResultFileRead(inputFilePath)).willReturn(fileInputStream);
			BDDMockito.given(csvServiceImpl.processFile(fileInputStream)).willReturn(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		syncAccountServiceMock.executeProcessFile(inputFilePath);
	}
	

}
