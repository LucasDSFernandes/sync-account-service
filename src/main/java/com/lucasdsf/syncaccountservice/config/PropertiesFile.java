package com.lucasdsf.syncaccountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesFile {
	
	@Value("${account.formats-length.agency}")
	private int accountAgencyLength;
	
	@Value("${account.formats-length.account}")
	private int accountLength;
	
	@Value("${account.file.outputFilePath}")
	private String outputFilePath;
	
	@Value("${account.file.path}")
	private String outputFileName;
	
	@Value("${account.file.extension}")
	private String outputFileExtension;

	public int getAgencyLength() {
		return accountAgencyLength;
	}

	public int getAccountLength() {
		return accountLength;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public String getOutputFileExtension() {
		return outputFileExtension;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}
}
