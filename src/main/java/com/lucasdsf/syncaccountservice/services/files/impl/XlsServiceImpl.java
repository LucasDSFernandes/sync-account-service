package com.lucasdsf.syncaccountservice.services.files.impl;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lucasdsf.syncaccountservice.services.files.FilesService;

@Component
@Qualifier("xlsFileService")
public class XlsServiceImpl implements FilesService{

	@Override
	public boolean processFile(FileInputStream inputDataParser) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public FileInputStream getResultFileInputStream(String inputFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

}
