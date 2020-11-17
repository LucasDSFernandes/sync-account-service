package com.lucasdsf.syncaccountservice.services.files.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
import com.lucasdsf.syncaccountservice.config.PropertiesFile;
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
	private PropertiesFile properties;
	@Autowired
	private AccountFormat accountFormat;

	/*
	 * Foi feito utilizando em paralelo do java 8, para esse exemplo seria caso a order de leitura do CSV não fosse necessaria. 
	 * Cada Thread pega uma linha, transforma os valores em objeto de Conta, mandava atualizar, escreve no arquivo de saida o resultado, e assim encerra o fluxo, 
	 * ate pegar outra linha.

	 * Poderia ser feito em apenas um unico fluxoo das Threads :,
	 * 1 ->  Em um mesmo loop em paralelo cada Thread Pega uma linha do arquivo Csv, valida o formato, manda atualizar no serviço de ReceitaService.
	 * 2 -> Ao obter a resposta do servico externo, escreve o resultado juntos com os dados da conta em um novo arquivo. 
	 * Para esse fluxo o loop ficaria muito carregado pois faria desde o processo de ler o arquivo, 
	 * chamar e aguardar o servico de atualizar conta, ate escrever em um arquivo novo.
	 * 
	 * Dividir em dois loops.
	 *  .1º loop: Em paralelo, cada thread pegava uma linha do Csv e adiciova em Lista de um objeto de conta.
	 *  .2º loop: Em paralelo, cada Thread pegava um objeto da lista feita acima, chamava o servico de atualizar 
	 *  e depois salvava no arquivo de saida, encerra o fluxo e pega o proximo Objeto da lista 
	 *  . O 2º loop poderia ser assincrono.
	 * */
	@Override
	public boolean processFile(FileInputStream fileInputStream) {
		LOGGER.info("Initializing file process");
		List<AccountInfoDTO> accountsInfoDto = new ArrayList<>();
		boolean isProcessed = false;

		CSVParser csvParser = convertFileInputStreamToCsvParser(fileInputStream);
		String outputFilePath = fileUtils.getOutputFilePath(properties.getOutputFileName(), properties.getOutputFileExtension());
		
		convertCsvParserToListAccountsInfo(accountsInfoDto, csvParser);
		
		try (FileWriter fileWriter = new FileWriter(new File(outputFilePath));) {
			csvHeaderWriter(fileWriter);
			csvBodyWriter(fileWriter, accountsInfoDto);
			
			fileWriter.flush();
			isProcessed = true;
		} catch (Exception e) {
			LOGGER.error("Error na gravação do arquivo Csv");
		}
		return isProcessed;
	}

	private void convertCsvParserToListAccountsInfo(List<AccountInfoDTO> accountsInfoDto, CSVParser csvParser) {
		try {
			csvParser.getRecords().parallelStream()
			.filter(c -> c.getRecordNumber() > 1)
			.forEach(csvRecord -> formatAccount(csvRecord, accountsInfoDto)	);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void csvHeaderWriter(FileWriter fileWriter) throws IOException {
		fileUtils.appendFile(fileWriter, getCsvHeader());
	}

	private String getCsvHeader() {
		return AccountFileEnum.getHeadersName().stream().collect(Collectors.joining(Constants.CSV_SEPARATOR)).concat(Constants.NEW_LINE_FUNCTION);		
	}

	/* 
	 *    Ao carregar o CSV pode ser tanto processando linha a linha ou mandar uma so lista para processamento
	 * */
	@Override
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
	
	public CSVParser convertFileInputStreamToCsvParser(InputStream fileInputStream) {
		try {
			LOGGER.info("Converting file to CSV format.");
			return CSVFormat.EXCEL.withDelimiter(';').parse(new InputStreamReader(fileInputStream));
		} catch (IOException e) {
			LOGGER.error("Error converting to CSV file.");
			LOGGER.error("Exception cause: {} ", e.getMessage());
		}
		return null ;
	}
	
	private void csvBodyWriter(FileWriter fileWriter, List<AccountInfoDTO> accountsInfoDto) {
		
		if(Objects.nonNull(accountsInfoDto) && !accountsInfoDto.isEmpty()) {
			IntegrationAccountService integrationAccount = new IntegrationAccountService();
			accountsInfoDto.parallelStream().forEach(accountInfoDto -> {
				writerCsvFile(fileWriter, accountInfoDto);
				integrationAccount.upgradingFromCentralBanckAccount(accountInfoDto);
			});
		}
	}

	private void writerCsvFile(FileWriter fileWriter, AccountInfoDTO accountInfoDto) {
		try {
			fileUtils.appendFile(fileWriter, buildCsvFileLine(accountInfoDto));
		} catch (IOException e) {
			LOGGER.error("Error writing csv file body");
		}
	}
	
	private String buildCsvFileLine(AccountInfoDTO accountData) {
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
	
	private List<AccountInfoDTO> formatAccount( CSVRecord csvRecord, List<AccountInfoDTO> accountsInfoDto) {
		String agency = csvRecord.get(AccountFileEnum.AGENCY.getIndex());
		String account = csvRecord.get(AccountFileEnum.ACCOUNT.getIndex());
		String balance = csvRecord.get(AccountFileEnum.BALANCE.getIndex());
		String status = csvRecord.get(AccountFileEnum.STATUS.getIndex());
		AccountInfoDTO accountInfoDto = null;
		try {
			if (!Strings.isNullOrEmpty(status) && !Strings.isNullOrEmpty(balance) && !Strings.isNullOrEmpty(account)
					&& !Strings.isNullOrEmpty(agency)) {
				LOGGER.info("Formatting the contents of the CSV file body");
				accountInfoDto = AccountBuilder.getInstance().setAgency(accountFormat.formatAgency(agency))
						.setAccount(accountFormat.formatAccount(account))
						.setBalance(accountFormat.formatBalance(balance))
						.setStatus(AccountStatusEnum.getByValue(status))
						.build();
				accountsInfoDto.add(accountInfoDto);
			}else {
				LOGGER.info("Content is empty or null");
			}
		} catch (Exception e) {
			LOGGER.error("Error formatting content");
		}
		return accountsInfoDto;
	}
}
