package com.lucasdsf.syncaccountservice.services.files;

import java.io.FileInputStream;

public interface FilesService {
	boolean processFile(FileInputStream fileInputStream);
	FileInputStream getResultFileInputStream(String inputFilePath);
}
