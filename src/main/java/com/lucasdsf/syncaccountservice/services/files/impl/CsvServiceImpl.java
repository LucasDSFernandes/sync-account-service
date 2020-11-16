package com.lucasdsf.syncaccountservice.services.files.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.lucasdsf.syncaccountservice.builders.AccountBuilder;
import com.lucasdsf.syncaccountservice.config.PropertiesFiles;
import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;
import com.lucasdsf.syncaccountservice.enums.AccountFileEnum;
import com.lucasdsf.syncaccountservice.services.account.IntegrationAccountService;
import com.lucasdsf.syncaccountservice.services.files.FilesService;
import com.lucasdsf.syncaccountservice.util.AccountFormat;
import com.lucasdsf.syncaccountservice.util.FileUtils;

@Component
@Qualifier("csvFileService")
public class CsvServiceImpl implements FilesService{
	private static final Logger LOGGER = LoggerFactory.getLogger(CsvServiceImpl.class);
	@Autowired
	private FileUtils fileUtils;
	@Autowired
	private PropertiesFiles properties;
	@Autowired
	private AccountFormat accountFormat;

	/*
	 * Foi feito utilizando em paralelo do java 8, para esse exemplo seria caso a order de leitura do CSV não fosse necessaria. 
	 * Cada Thread pega uma linha, transforma os valores em objeto de Conta, mandava atualizar, escreve no arquivo de saida o resultado, e assim encerra o fluxo, 
	 * ate pegar outra linha.

	 * Poderia ser dividido em dois fluxos das Threads:,
	 *  .primeiro leria e transformava a linha do arquivo de entrada em Lista de objeto, encerra fluxo. Ate pegar outra linha e repetir
	 *  .segundo pegava a lista feita acima, e cada Thread pegava um objeto da lista em pararelo chamava o servico de atualizar 
	 *  e depois salvava no arquivo de saida, encerra o fluxo e pega o proximo Objeto da lista -> poderia ser utilizando assincrono.
	 * */
	@Override
	public boolean processFile(FileInputStream fileInputStream) {
		LOGGER.info("Initializing file process");
		boolean isProcessed = false;
		CSVParser inputFileStreamCsvParser = parseImputStreamCSV(fileInputStream);
		String outputFilePath = fileUtils.createOutputFile(properties.getOutputFileName(),
				properties.getOutputFileExtension());

		try (FileWriter fileWriter = new FileWriter(new File(outputFilePath));) {
			fileUtils.appenndeWriterFile(fileWriter, getCsvHeader());

			inputFileStreamCsvParser.getRecords().parallelStream().filter(c -> c.getRecordNumber() > 1)
					.forEach(csvRecord -> {
						try {
							LOGGER.info("Building the CSV body. Linha: {}. Thread Current: {}",
									csvRecord.getRecordNumber(), Thread.currentThread().getName());
							setCsvBody(csvRecord, fileWriter);
						} catch (IOException e) {
						}
					});

			fileWriter.flush();
			fileWriter.close();
			isProcessed = true;
		} catch (Exception e) {
			LOGGER.error("Error na gravação do arquivo Csv");
		}
		return isProcessed;
	}

	private String getCsvHeader() {
		return AccountFileEnum.getHeadersName().stream().collect(Collectors.joining(Constants.CSV_SEPARATOR)).concat(Constants.NEW_LINE_FUNCTION);		
	}

	/* 
	 *    Ao carregar o CSV pode ser tanto processando linha a linha ou mandar uma so lista para processamento
	 * */
	@Override
	public FileInputStream getResultFileRead(String inputFilePath) {
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
	
	public CSVParser parseImputStreamCSV(InputStream fileInputStream) {
		try {
			LOGGER.info("Converting file to CSV format.");
			return CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(fileInputStream));
		} catch (IOException e) {
			LOGGER.error("Error converting to CSV file.");
			LOGGER.error("Exception cause -> {} ", e.getCause());
		}
		return null ;
	}
	
	private void setCsvBody(CSVRecord csvRecord, FileWriter fileWriter) throws IOException {
		AccountInfoDTO accountData = formatAccount(csvRecord);
		
		if(Objects.nonNull(accountData)) {
			IntegrationAccountService integrationAccount = new IntegrationAccountService();
			integrationAccount.upgradingFromCentralBanckAccount(accountData);
			
			fileUtils.appenndeWriterFile(fileWriter, appendLineToCsvFile(accountData));
		}
	}
	
	private String appendLineToCsvFile(AccountInfoDTO accountData) {
		
		StringBuilder csvStringBuilder=  new StringBuilder();
		csvStringBuilder.append(accountData.getAgencia());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(accountData.getConta());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(accountData.getSaldo());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(accountData.getStatus());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(accountData.getProcessedStatus());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(accountData.getProcessError());
		csvStringBuilder.append(Constants.CSV_SEPARATOR);
		csvStringBuilder.append(Constants.NEW_LINE_FUNCTION);
		return csvStringBuilder.toString();
	}
	
	private AccountInfoDTO formatAccount( CSVRecord csvRecord) {
		String agency = csvRecord.get(AccountFileEnum.AGENCY.getIndex());
		String account = csvRecord.get(AccountFileEnum.ACCOUNT.getIndex());
		String balance = csvRecord.get(AccountFileEnum.BALANCE.getIndex());
		String status = csvRecord.get(AccountFileEnum.STATUS.getIndex());
		AccountInfoDTO accountInfoDTO = null;
		try {
			if (!Strings.isNullOrEmpty(status) && !Strings.isNullOrEmpty(balance) && !Strings.isNullOrEmpty(account) && !Strings.isNullOrEmpty(agency)) {
				LOGGER.info("Formatting the contents of the CSV file body");
				accountInfoDTO = AccountBuilder.getInstance().setAgency(accountFormat.formatAgency(agency))
						.setAccount(accountFormat.formatAccount(account))
						.setBalance(accountFormat.formatBalance(balance))
						.setStatus(AccountStatusEnum.getByValue(status))
						.build();
			}else {
				LOGGER.info("Content is empty or null");
			}
		} catch (Exception e) {
			LOGGER.error("Error formatting content");
		}
		return accountInfoDTO;
	}
}
