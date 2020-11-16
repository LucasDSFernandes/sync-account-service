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
import com.lucasdsf.syncaccountservice.services.files.impl.XlsServiceImpl;

@Component
public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	@Autowired 
	private	CsvServiceImpl csvServiceImpl;
	@Autowired 
	private XlsServiceImpl xlsServiceImpl;
	
	Map<String, FilesService> strategyFiles ;
	
	public String createOutputFile(String outputFileName, String outputFileExtension ) {
		String path = "";
		try {
			File outputFile = new File(getFilePath(outputFileName, outputFileExtension));
			if ( !outputFile.exists() ) {
				LOGGER.info("Creating output file.");
				outputFile.createNewFile();
				path = outputFile.getAbsolutePath();
			}else {
				LOGGER.error("Path file {} not created." , getFilePath(outputFileName, outputFileExtension));
			}
		} catch (Exception e) {
			LOGGER.error("Error creating output file.");
		}
		
		return path;
	}

	private String getFilePath(String outputPath, String outputFileExtension) {
		return System.getProperty("user.dir").concat(outputPath)
				.concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_STAMP_FORMAT)))
				.concat(outputFileExtension);
	}

	public Writer appenndeWriterFile(FileWriter fileWriter, String content) throws IOException {
		LOGGER.info("Writing data file");
		return fileWriter.append(content);
	}
	/*
	 * Codigo generico para caso no futuro precisar usar uma outra extenção, 
	 * so bastara implementar  dentro da sua classe da extenção como ler e processar
	 * */

	public FileUtils putStrategyFile() {
		strategyFiles = new HashMap<String, FilesService>();
		strategyFiles.put("csv", csvServiceImpl );
		strategyFiles.put("xls", xlsServiceImpl );
		return this;
	}
	
	public FilesService getFile(String inputPathFile) {
		LOGGER.info("Checking file extension {} .", inputPathFile);
		return strategyFiles.get(FilenameUtils.getExtension(inputPathFile));
	}
}

