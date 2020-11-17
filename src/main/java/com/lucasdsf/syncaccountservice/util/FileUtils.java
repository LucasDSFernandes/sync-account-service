package com.lucasdsf.syncaccountservice.util;

import java.io.File;
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

import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.services.files.FilesService;
import com.lucasdsf.syncaccountservice.services.files.impl.CsvServiceImpl;

@Component
public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	@Autowired 
	private	CsvServiceImpl csvServiceImpl;
	
	Map<String, FilesService> fileStrategyMap = new HashMap<>();;
	
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
	 * 
	 * Exemplo:
	 * fileStrategyMap.put("xls", xlsServiceImpl );
	 * fileStrategyMap.put("xlsx", xlsxServiceImpl );
	 * fileStrategyMap.put("txt", txtServiceImpl );
	 * */
	
	public FileUtils putStrategyFile() {
		fileStrategyMap.put("csv", csvServiceImpl );
		return this;
	}

	public FilesService getStrategyFile(String inputPathFile) {
		LOGGER.info("Checking file extension {} .", inputPathFile);
		return fileStrategyMap.get(FilenameUtils.getExtension(inputPathFile));
	}
}

