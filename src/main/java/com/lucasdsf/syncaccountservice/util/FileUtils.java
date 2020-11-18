package com.lucasdsf.syncaccountservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.services.files.FilesService;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;

@Component
public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	
	@Autowired 
	private	CsvServiceImpl csvServiceImpl;
	
	private Map<String, FilesService> fileStrategyMap = new HashMap<>();;
	
	public String getOutputFilePath(String outputFileName, String outputFileExtension ) {
		String outputFilePath ="";
		try {
			File outputFile = new File(getFilePathWithTimeStamp(outputFileName, outputFileExtension));
			if ( !outputFile.exists() && outputFile.createNewFile() ) {
				LOGGER.info("Creating output file.");
				outputFilePath = outputFile.getAbsolutePath();
			}else {
				LOGGER.error("Path file {} not created." , getFilePathWithTimeStamp(outputFileName, outputFileExtension));
			}
		} catch (Exception e) {
			LOGGER.error("Error creating output file.");
		}
		
		return outputFilePath;
	}

	public FileInputStream getResultFileInputStream(String inputFilePath) {
		LOGGER.info("Initializing file reading");
		FileInputStream fileInputStream = null;
		if ( !Strings.isNullOrEmpty(inputFilePath) ) {
			try {
				 fileInputStream = new FileInputStream( new File(inputFilePath));
				LOGGER.info("Csv File Path: {}", inputFilePath);
			} catch (IOException e) {
				LOGGER.error("Error reading file");
			}
		} else {
			LOGGER.info("Input File Null or is Empty");
		}
		return fileInputStream;
	}
	
	private String getFilePathWithTimeStamp(String outputFileName, String outputFileExtension) {
		return System.getProperty("user.dir").concat(outputFileName)
				.concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_STAMP_FORMAT)))
				.concat(outputFileExtension);
	}

	public Writer appendFile(FileWriter fileWriter, String content) throws IOException {
		LOGGER.info("Writing data file");
		return fileWriter.append(content);
	}
	
	/*
	 * 1 - Para caso no futuro precisar usar uma outra extenção de arquivo,  basta adicionar uma put no map com: 
     *      Key -> extenção do arquivo 
     *      Value -> instancia da classe da extenção
     * 2 - Implementar regra na classe da extenção
     * 3 - A nova classe de implementação do service criado deve implementar a interface FilesService.
	 * 
	 * Exemplo:
	 * fileStrategyMap.put("xls", xlsServiceImpl );
	 * fileStrategyMap.put("xlsx", xlsxServiceImpl );
	 * fileStrategyMap.put("txt", txtServiceImpl );
	 * 
	 * */
	
	public FileUtils putStrategyFile() {
		fileStrategyMap.put("csv", csvServiceImpl );
		return this;
	}

	public FilesService getStrategyFile(String inputPathFile) {
		LOGGER.info("Checking file extension {} .", inputPathFile);
		return fileStrategyMap.get(FilenameUtils.getExtension(inputPathFile));
	}
	
	public void closeFile(FileInputStream fileInputStream) {
		try {
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String buildFileLine(AccountInfoDTO accountData, String separator) {
		StringBuilder csvStringBuilder=  new StringBuilder();
		csvStringBuilder.append(accountData.getAgencia());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(accountData.getConta());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(accountData.getSaldo());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(accountData.getStatus());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(accountData.getProcessedStatus());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(accountData.getProcessError());
		csvStringBuilder.append(separator);
		csvStringBuilder.append(Constants.NEW_LINE_FUNCTION);
		return csvStringBuilder.toString();
	}
}

