package com.lucasdsf.syncaccountservice.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.lucasdsf.syncaccountservice.builders.AccountBuilder;
import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountFileEnum;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;

@Component
public class CsvUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);
	
	@Autowired
	private FileUtils fileUtils;
	
	@Autowired
	private AccountFormat accountFormat;
	
	public void convertCsvParserToListAccountsInfo(List<AccountInfoDTO> accountsInfoDto, CSVParser csvParser)
			throws IOException {
		csvParser.getRecords().parallelStream().filter(c -> c.getRecordNumber() > 1)
				.forEach(csvRecord -> formatAccount(csvRecord, accountsInfoDto));
	}
	
	/* 
	 *    Ao carregar o CSV pode ser tanto processando linha a linha ou mandar uma so lista para processamento
	 * */
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
	
	private List<AccountInfoDTO> formatAccount(CSVRecord csvRecord, List<AccountInfoDTO> accountsInfoDto) {
		String agency = getCsvRecord(csvRecord, AccountFileEnum.AGENCY.getIndex());
		String account = getCsvRecord(csvRecord, AccountFileEnum.ACCOUNT.getIndex());
		String balance = getCsvRecord(csvRecord, AccountFileEnum.BALANCE.getIndex());
		String status = getCsvRecord(csvRecord, AccountFileEnum.STATUS.getIndex());

		if (isValidDataToProcess(agency, account, balance, status)) {

			LOGGER.info("Formatting the contents of the CSV file body");

			AccountInfoDTO accountInfoDto = AccountBuilder.getInstance().setAgency(accountFormat.formatAgency(agency))
					.setAccount(accountFormat.formatAccount(account))
					.setBalance(accountFormat.formatBalance(balance))
					.setStatus(AccountStatusEnum.getByValue(status)).build();

			accountsInfoDto.add(accountInfoDto);
		} else {
			LOGGER.info("Content is empty or null");
		}

		return accountsInfoDto;
	}

	private boolean isValidDataToProcess(String agency, String account, String balance, String status) {
		return !Strings.isNullOrEmpty(status) 
				&& !Strings.isNullOrEmpty(balance) 
				&& !Strings.isNullOrEmpty(account)
				&& !Strings.isNullOrEmpty(agency);
	}
	
	private String getCsvRecord(CSVRecord csvRecord, int index) {
		try {
			return csvRecord.get(index);
		} catch (Exception e) {
			LOGGER.error("Error getting data from csv file");
		}
		return null;
	}
	
	public void writerCsvFile(FileWriter fileWriter, AccountInfoDTO accountInfoDto) {
		try {
			fileUtils.appendFile(fileWriter, fileUtils.buildFileLine(accountInfoDto, Constants.CSV_SEPARATOR));
		} catch (IOException e) {
			LOGGER.error("Error writing csv file body");
		}
	}
	
	public void csvHeaderWriter(FileWriter fileWriter) throws IOException {
		fileUtils.appendFile(fileWriter, getCsvHeader());
	}

	private String getCsvHeader() {
		return AccountFileEnum.getHeadersName().stream().collect(Collectors.joining(Constants.CSV_SEPARATOR)).concat(Constants.NEW_LINE_FUNCTION);		
	}
	
}

