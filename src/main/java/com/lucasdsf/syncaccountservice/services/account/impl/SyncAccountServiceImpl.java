package com.lucasdsf.syncaccountservice.services.account.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasdsf.syncaccountservice.services.account.SyncAccountService;
import com.lucasdsf.syncaccountservice.services.files.FilesService;
import com.lucasdsf.syncaccountservice.util.FileUtils;

@Service
public class SyncAccountServiceImpl implements SyncAccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SyncAccountServiceImpl.class);

	@Autowired
	private FileUtils fileUtil;

	@Override
	public void executeProcessFile(String inputPathFile) {
		FilesService fileService = fileUtil.putStrategyFile().getStrategyFile(inputPathFile);
		
		if (Objects.nonNull(fileService)) {

			fileService.processFile(inputPathFile);
		} else {
			LOGGER.error("File not found or incompatible file extension.");
		}

	}
}
